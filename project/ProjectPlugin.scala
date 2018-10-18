import com.typesafe.sbt.site.jekyll.JekyllPlugin.autoImport._
import microsites._
import microsites.MicrositesPlugin.autoImport._
import sbt.Keys._
import sbt._
import sbtorgpolicies.model._
import sbtorgpolicies.OrgPoliciesKeys.orgBadgeListSetting
import sbtorgpolicies.OrgPoliciesPlugin
import sbtorgpolicies.OrgPoliciesPlugin.autoImport._
import sbtorgpolicies.templates.badges._
import sbtorgpolicies.runnable.syntax._
import scoverage.ScoverageKeys
import scoverage.ScoverageKeys._
import tut.TutPlugin.autoImport._

object ProjectPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def requires: Plugins = OrgPoliciesPlugin

  object autoImport {

    lazy val micrositeSettings = Seq(
      micrositeName := "Github4s",
      micrositeDescription := "Github API wrapper written in Scala",
      micrositeBaseUrl := "github4s",
      micrositeDocumentationUrl := "/github4s/docs.html",
      micrositeGithubOwner := "47deg",
      micrositeGithubRepo := "github4s",
      micrositeAuthor := "Github4s contributors",
      micrositeOrganizationHomepage := "https://github.com/47deg/github4s/blob/master/AUTHORS.md",
      micrositeExtraMdFiles := Map(
        file("CHANGELOG.md") -> ExtraMdFileConfig(
          "changelog.md",
          "page",
          Map("title" -> "Changelog", "section" -> "changelog", "position" -> "2")
        )
      ),
      includeFilter in Jekyll := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.md",
      scalacOptions in Tut ~= (_ filterNot Set("-Ywarn-unused-import", "-Xlint").contains)
    )

    lazy val testSettings = Seq(
      fork in Test := false
    )

    lazy val commonCrossDeps = Seq(
      %%("cats-core"),
      %%("cats-free"),
      %%("simulacrum"),
      %%("circe-core"),
      %%("circe-generic"),
      %%("circe-parser"),
      %%("base64"),
      %%("scalamockScalatest") % "test",
      %%("scalatest")          % "test"
    )

    lazy val standardCommonDeps = Seq(
      libraryDependencies += compilerPlugin(%%("paradise") cross CrossVersion.full)
    )

    lazy val jvmDeps = Seq(
      libraryDependencies ++= Seq(
        %%("scalaj"),
        "org.mock-server" % "mockserver-netty" % "3.10.4" % "test" excludeAll ExclusionRule(
          "com.twitter")
      )
    )

    lazy val jsDeps: Def.Setting[Seq[ModuleID]] = libraryDependencies += %%%("roshttp")

    lazy val docsDependencies: Def.Setting[Seq[ModuleID]] = libraryDependencies += %%("scalatest")

    lazy val scalazDependencies: Def.Setting[Seq[ModuleID]] =
      libraryDependencies += %%("scalaz-concurrent")


    lazy val catsEffectDependencies: Seq[ModuleID] =
      Seq(
        "org.typelevel" %% "cats-effect" % "1.0.0",
        "org.http4s" %% "http4s-dsl" % "0.19.0",
        "org.http4s" %% "http4s-blaze-client" % "0.19.0",
        "org.http4s" %% "http4s-circe" % "0.19.0",
        "org.typelevel" %% "cats-tagless-macros" % "0.1.0",
        %%("scalatest") % "test"
      )

    def toCompileTestList(sequence: Seq[ProjectReference]): List[String] = sequence.toList.map {
      p =>
        val project: String = p.asInstanceOf[LocalProject].project
        s"$project/test"
    }
  }

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      name := "github4s",
      orgProjectName := "Github4s",
      description := "Github API wrapper written in Scala",
      startYear := Option(2016),
      resolvers += Resolver.sonatypeRepo("snapshots"),
      scalaVersion := scalac.`2.12`,
      crossScalaVersions := scalac.crossScalaVersions,
      scalacOptions ~= (_ filterNot Set("-Xlint").contains),
      orgGithubTokenSetting := "GITHUB4S_ACCESS_TOKEN",
      orgBadgeListSetting := List(
        TravisBadge.apply(_),
        GitterBadge.apply(_),
        CodecovBadge.apply(_),
        MavenCentralBadge.apply(_),
        LicenseBadge.apply(_),
        ScalaLangBadge.apply(_),
        ScalaJSBadge.apply(_),
        GitHubIssuesBadge.apply(_)
      ),
      orgSupportedScalaJSVersion := Some("0.6.21"),
      orgScriptTaskListSetting ++= List(
        (ScoverageKeys.coverageAggregate in Test).asRunnableItemFull,
        "docs/tut".asRunnableItem
      ),
      coverageExcludedPackages := "<empty>;github4s\\.scalaz\\..*",
      // This is necessary to prevent packaging the BuildInfo with
      // sensible information like the Github token. Do not remove.
      mappings in (Compile, packageBin) ~= { (ms: Seq[(File, String)]) =>
        ms filter {
          case (_, toPath) =>
            !toPath.startsWith("github4s/BuildInfo")
        }
      }
    ) ++ shellPromptSettings
}
