import de.undercouch.gradle.tasks.download.DownloadAction
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class DownloadFirefoxTask : DefaultTask() {
    @get:Input
    abstract val version: Property<String>

    @get:Internal
    abstract val outputBin: RegularFileProperty

    @TaskAction
    fun run() {
        val version = version.get()
        val gradleHome = project.gradle.gradleUserHomeDir
        val destDir = "$gradleHome/firefox/$version"
        val downloadDest = "$destDir/firefox.tar.bz2"

        DownloadAction(project, this).apply {
            src("https://download-installer.cdn.mozilla.net/pub/firefox/releases/$version/linux-x86_64/en-US/firefox-$version.tar.bz2")
            dest(downloadDest)
            overwrite(false)
        }.execute().get()

        outputBin.set(File(destDir, "firefox/firefox"))

        // skip unpack if it exists
        if (File(destDir, "firefox").exists()) return

        project.copy {
            from(project.tarTree(downloadDest))
            into(destDir)
        }
    }
}
