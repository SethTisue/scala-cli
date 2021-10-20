"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[7898],{3905:function(e,t,n){n.d(t,{Zo:function(){return u},kt:function(){return m}});var r=n(7294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var l=r.createContext({}),c=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},u=function(e){var t=c(e.components);return r.createElement(l.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},d=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,l=e.parentName,u=s(e,["components","mdxType","originalType","parentName"]),d=c(n),m=o,f=d["".concat(l,".").concat(m)]||d[m]||p[m]||i;return n?r.createElement(f,a(a({ref:t},u),{},{components:n})):r.createElement(f,a({ref:t},u))}));function m(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,a=new Array(i);a[0]=d;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s.mdxType="string"==typeof e?e:o,a[1]=s;for(var c=2;c<i;c++)a[c]=n[c];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}d.displayName="MDXCreateElement"},9328:function(e,t,n){n.r(t),n.d(t,{frontMatter:function(){return a},contentTitle:function(){return s},metadata:function(){return l},toc:function(){return c},default:function(){return p}});var r=n(7462),o=n(3366),i=(n(7294),n(3905)),a={title:"Setup IDE",sidebar_position:15},s=void 0,l={unversionedId:"commands/setup-ide",id:"commands/setup-ide",isDocsHomePage:!1,title:"Setup IDE",description:"Scala CLI can help you setup your IDE of choice (whether it is VS Code or IntelliJ)",source:"@site/docs/commands/setup-ide.md",sourceDirName:"commands",slug:"/commands/setup-ide",permalink:"/docs/commands/setup-ide",editUrl:"https://github.com/Virtuslab/scala-cli/edit/master/website/docs/commands/setup-ide.md",tags:[],version:"current",sidebarPosition:15,frontMatter:{title:"Setup IDE",sidebar_position:15},sidebar:"tutorialSidebar",previous:{title:"Format",permalink:"/docs/commands/fmt"},next:{title:"Clean",permalink:"/docs/commands/clean"}},c=[{value:"IDE support internals",id:"ide-support-internals",children:[]}],u={toc:c};function p(e){var t=e.components,n=(0,o.Z)(e,["components"]);return(0,i.kt)("wrapper",(0,r.Z)({},u,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("p",null,"Scala CLI can help you setup your IDE of choice (whether it is VS Code or IntelliJ)\nby generating files necessary for it to provide you with full-blown support.\nUsing Scala CLI should be as simple as possible,\ntherefore under the hood this command is also run before first ",(0,i.kt)("inlineCode",{parentName:"p"},"run"),", ",(0,i.kt)("inlineCode",{parentName:"p"},"compile")," or ",(0,i.kt)("inlineCode",{parentName:"p"},"test")," commands.\nAs a result in most cases you do not need to run this command manually. "),(0,i.kt)("p",null,"You can invoke ",(0,i.kt)("inlineCode",{parentName:"p"},"setup-ide")," like:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-bash"},"scala-cli setup-ide . --scala 2.13\n")),(0,i.kt)("p",null,"Please keep in mind that if you change any of those options it may be required to restart or reimport\nthe project within IDE."),(0,i.kt)("h3",{id:"ide-support-internals"},"IDE support internals"),(0,i.kt)("p",null,"After invoking ",(0,i.kt)("inlineCode",{parentName:"p"},"setup-ide")," two files should be generated:"),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},".bsp/scala-cli.json")," "),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("inlineCode",{parentName:"li"},".scala/ide-options.json"))),(0,i.kt)("p",null,"First one is a file specifically created for Build Server Protocol (BSP) support in your IDE.\nThis protocol is supported by two most popular IDEs: VS Code (with Metals extension) and IntelliJ (with Scala plugin)\nand defines a way in which IDEs gather information about the project you are working on."),(0,i.kt)("p",null,"Second file is designed to store settings used by Scala CLI while generating BSP configuration.\nThis covers all options like scala version, custom arguments and more but fortunatelly you shouldn't\nbe forced to edit it."))}p.isMDXComponent=!0}}]);