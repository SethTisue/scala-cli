package scala.build.internal

import coursier.jvm.Execve
import sbt.testing.{Framework, Status}

import java.io.File
import java.nio.file.{Files, Path, Paths}

import scala.build.EitherCps.{either, value}
import scala.build.Logger
import scala.build.errors.{
  NoFrameworkFoundByBridgeError,
  NoTestFrameworkFoundError,
  NoTestsRun,
  TestError,
  TooManyFrameworksFoundByBridgeError
}
import scala.build.testrunner.{AsmTestRunner, TestRunner}
import scala.util.Properties

object Runner {

  def run(
    commandName: String,
    command: Seq[String],
    logger: Logger,
    allowExecve: Boolean = false,
    cwd: Option[os.Path] = None
  ): Int = {

    import logger.{log, debug}

    log(
      s"Running ${command.mkString(" ")}",
      "  Running" + System.lineSeparator() +
        command.iterator.map(_ + System.lineSeparator()).mkString
    )

    if (allowExecve && Execve.available()) {
      debug("execve available")

      for (dir <- cwd)
        Chdir.chdir(dir.toString)

      Execve.execve(
        findInPath(command.head).fold(command.head)(_.toString),
        commandName +: command.tail.toArray,
        sys.env.toArray.sorted.map { case (k, v) => s"$k=$v" }
      )
      sys.error("should not happen")
    }
    else {
      val b = new ProcessBuilder(command: _*)
        .inheritIO()
      for (dir <- cwd)
        b.directory(dir.toIO)
      b.start().waitFor()
    }
  }

  def runJvm(
    javaCommand: String,
    javaArgs: Seq[String],
    classPath: Seq[File],
    mainClass: String,
    args: Seq[String],
    logger: Logger,
    allowExecve: Boolean = false
  ): Int = {

    val command =
      Seq(javaCommand) ++
        javaArgs ++
        Seq(
          "-cp",
          classPath.iterator.map(_.getAbsolutePath).mkString(File.pathSeparator),
          mainClass
        ) ++
        args

    run("java", command, logger, allowExecve)
  }

  private def endsWithCaseInsensitive(s: String, suffix: String): Boolean =
    s.length >= suffix.length &&
    s.regionMatches(true, s.length - suffix.length, suffix, 0, suffix.length)

  private def findInPath(app: String): Option[Path] = {
    val asIs = Paths.get(app)
    if (Paths.get(app).getNameCount >= 2) Some(asIs)
    else {
      def pathEntries =
        Option(System.getenv("PATH"))
          .iterator
          .flatMap(_.split(File.pathSeparator).iterator)
      def pathSep =
        if (Properties.isWin)
          Option(System.getenv("PATHEXT"))
            .iterator
            .flatMap(_.split(File.pathSeparator).iterator)
        else Iterator("")
      def matches = for {
        dir <- pathEntries
        ext <- pathSep
        app0 = if (endsWithCaseInsensitive(app, ext)) app else app + ext
        path = Paths.get(dir).resolve(app0)
        if Files.isExecutable(path)
      } yield path
      matches.take(1).toList.headOption
    }
  }

  def runJs(
    entrypoint: File,
    args: Seq[String],
    logger: Logger,
    allowExecve: Boolean = false
  ): Int = {

    import logger.{log, debug}

    val nodePath = findInPath("node").fold("node")(_.toString)
    val command  = Seq(nodePath, entrypoint.getAbsolutePath) ++ args

    log(
      s"Running ${command.mkString(" ")}",
      "  Running" + System.lineSeparator() +
        command.iterator.map(_ + System.lineSeparator()).mkString
    )

    if (allowExecve && Execve.available()) {
      debug("execve available")
      Execve.execve(
        command.head,
        "node" +: command.tail.toArray,
        sys.env.toArray.sorted.map { case (k, v) => s"$k=$v" }
      )
      sys.error("should not happen")
    }
    else
      new ProcessBuilder(command: _*)
        .inheritIO()
        .start()
        .waitFor()
  }

  def runNative(
    launcher: File,
    args: Seq[String],
    logger: Logger,
    allowExecve: Boolean = false
  ): Int = {

    import logger.{log, debug}

    val command = Seq(launcher.getAbsolutePath) ++ args

    log(
      s"Running ${command.mkString(" ")}",
      "  Running" + System.lineSeparator() +
        command.iterator.map(_ + System.lineSeparator()).mkString
    )

    if (allowExecve && Execve.available()) {
      debug("execve available")
      Execve.execve(
        command.head,
        launcher.getName +: command.tail.toArray,
        sys.env.toArray.sorted.map { case (k, v) => s"$k=$v" }
      )
      sys.error("should not happen")
    }
    else
      new ProcessBuilder(command: _*)
        .inheritIO()
        .start()
        .waitFor()
  }

  private def runTests(
    classPath: Seq[Path],
    framework: Framework,
    requireTests: Boolean,
    args: Seq[String],
    parentInspector: AsmTestRunner.ParentInspector
  ): Either[NoTestsRun, Boolean] = {

    val taskDefs =
      AsmTestRunner.taskDefs(
        classPath,
        keepJars = false,
        framework.fingerprints(),
        parentInspector
      ).toArray

    val runner       = framework.runner(args.toArray, Array(), null)
    val initialTasks = runner.tasks(taskDefs)
    val events       = TestRunner.runTasks(initialTasks, System.out)

    val doneMsg = runner.done()
    if (doneMsg.nonEmpty)
      System.out.println(doneMsg)

    if (requireTests && events.isEmpty)
      Left(new NoTestsRun)
    else
      Right {
        !events.exists { ev =>
          ev.status == Status.Error ||
          ev.status == Status.Failure ||
          ev.status == Status.Canceled
        }
      }
  }

  private def frameworkName(
    classPath: Seq[Path],
    parentInspector: AsmTestRunner.ParentInspector
  ): Either[NoTestFrameworkFoundError, String] = {
    val fwOpt = AsmTestRunner.findFrameworkService(classPath)
      .orElse {
        AsmTestRunner.findFramework(
          classPath,
          TestRunner.commonTestFrameworks,
          parentInspector
        )
      }
    fwOpt match {
      case Some(fw) => Right(fw.replace('/', '.').replace('\\', '.'))
      case None     => Left(new NoTestFrameworkFoundError)
    }
  }

  def testJs(
    classPath: Seq[Path],
    entrypoint: File,
    requireTests: Boolean,
    args: Seq[String],
    testFrameworkOpt: Option[String],
    logger: Logger
  ): Either[TestError, Int] = either {
    import org.scalajs.jsenv.Input
    import org.scalajs.jsenv.nodejs.NodeJSEnv
    import org.scalajs.logging.ScalaConsoleLogger
    import org.scalajs.testing.adapter.TestAdapter
    val nodePath = findInPath("node").fold("node")(_.toString)
    val jsEnv = new NodeJSEnv(
      NodeJSEnv.Config()
        .withExecutable(nodePath)
        .withArgs(Nil)
        .withEnv(Map.empty)
        .withSourceMap(NodeJSEnv.SourceMap.Disable)
    )
    val adapterConfig        = TestAdapter.Config().withLogger(new ScalaConsoleLogger)
    val inputs               = Seq(Input.Script(entrypoint.toPath))
    var adapter: TestAdapter = null

    logger.debug(s"JS tests class path: $classPath")

    val parentInspector = new AsmTestRunner.ParentInspector(classPath)
    val frameworkName0 = testFrameworkOpt match {
      case Some(fw) => fw
      case None     => value(frameworkName(classPath, parentInspector))
    }

    val res =
      try {
        adapter = new TestAdapter(jsEnv, inputs, adapterConfig)

        val frameworks = adapter.loadFrameworks(List(List(frameworkName0))).flatten

        if (frameworks.isEmpty)
          Left(new NoFrameworkFoundByBridgeError)
        else if (frameworks.length > 1)
          Left(new TooManyFrameworksFoundByBridgeError)
        else {
          val framework = frameworks.head
          runTests(classPath, framework, requireTests, args, parentInspector)
        }
      }
      finally if (adapter != null) adapter.close()

    if (value(res)) 0
    else 1
  }

  def testNative(
    classPath: Seq[Path],
    launcher: File,
    frameworkNameOpt: Option[String],
    requireTests: Boolean,
    args: Seq[String],
    logger: Logger
  ): Either[TestError, Int] = either {

    import scala.scalanative.testinterface.adapter.TestAdapter

    logger.debug(s"Native tests class path: $classPath")

    val parentInspector = new AsmTestRunner.ParentInspector(classPath)
    val frameworkName0 = frameworkNameOpt match {
      case Some(fw) => fw
      case None     => value(frameworkName(classPath, parentInspector))
    }

    val config = TestAdapter.Config()
      .withBinaryFile(launcher)
      .withEnvVars(sys.env.toMap)
      .withLogger(logger.scalaNativeLogger)

    var adapter: TestAdapter = null

    val res =
      try {
        adapter = new TestAdapter(config)

        val frameworks = adapter.loadFrameworks(List(List(frameworkName0))).flatten

        if (frameworks.isEmpty)
          Left(new NoFrameworkFoundByBridgeError)
        else if (frameworks.length > 1)
          Left(new TooManyFrameworksFoundByBridgeError)
        else {
          val framework = frameworks.head
          runTests(classPath, framework, requireTests, args, parentInspector)
        }
      }
      finally if (adapter != null) adapter.close()

    if (value(res)) 0
    else 1
  }
}
