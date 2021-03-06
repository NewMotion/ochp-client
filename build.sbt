import com.newmotion.sbt.plugins.SoapUIMockServicePlugin.soapui
import sbt._

val cxfVer = "3.4.2"

def cxfRt(lib: String) =
  "org.apache.cxf" % s"cxf-rt-$lib" % cxfVer

def specs(lib: String) =
  "org.specs2" %% s"specs2-$lib" % "4.10.0"

val ochp = (project in file("."))
  .enablePlugins(OssLibPlugin, CxfWsdl2JavaPlugin, SoapUIMockServicePlugin)
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    soapui.settings,
    organization := "com.newmotion",
    name := "ochp-client",
    moduleName := name.value,
    scalaVersion := tnm.ScalaVersion.curr,
    crossScalaVersions := Seq(tnm.ScalaVersion.curr, tnm.ScalaVersion.prev),
    libraryDependencies ++= Seq(
      cxfRt("frontend-jaxws"),
      cxfRt("transports-http"),
      cxfRt("ws-security"),
      "commons-collections" % "commons-collections" % "3.2.2",
      "com.sun.xml.messaging.saaj" % "saaj-impl" % "1.5.2",
      "com.github.nscala-time" %% "nscala-time" % "2.24.0",
      "org.slf4j" % "slf4j-api" % "1.7.30",
      "com.typesafe" % "config" % "1.4.1" % "it,test",
      specs("junit") % "it,test",
      specs("mock") % "it,test"
    ),
    cxfVersion := cxfVer,
    dependencyOverrides += "org.apache.cxf.xjcplugins" % "cxf-xjc-ts" % "3.3.1",
    cxfWsdls := Seq(
      CxfWsdl((resourceDirectory in Compile).value / "wsdl" / "ochp-1.3.wsdl",
              Seq("-validate", "-xjc-verbose"),
              "ochp")
    ),
    fork in IntegrationTest := true,
    soapui.mockServices := Seq(
      soapui.MockService(
        (resourceDirectory in IntegrationTest).value / "soapui" / "OCHP-1-3-soapui-project.xml",
        "8088")
    ),
    mappings in (Compile, packageSrc) ++= Path
      .allSubpaths(target.value / "cxf" / "ochp")
      .toSeq
  )

val ochpCommandLine = (project in file("cmdline"))
  .enablePlugins(AppPlugin)
  .dependsOn(ochp)
  .settings(
    organization := "com.newmotion",
    name := "ochp-client-cmdline"
  )