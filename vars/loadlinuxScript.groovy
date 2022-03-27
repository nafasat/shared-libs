def call(Map config = [:]) {
  def scriptcontents = libraryResource "com/jenkins/script/github/linux/${config.name}"
  writeFile file "${config.name}", text: scriptcontents
  sh "chmod a+x ./${config.name}"
}
