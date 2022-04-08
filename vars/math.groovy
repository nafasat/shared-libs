def add(x,y) {
  echo "Sum of ${x} and ${y} is ${x+y}"
}

def hostname() {
  sh '''hostname -f'''
}

def sftp_get() {
  sh "echo -oStrictHostKeyChecking=no -i ${keyfile} ${USERNAME}"
}

def test_if_condition(Map config=[:]) {
  if ("${config.name}"=='') {
    config.name="Ahmed"
    sh "echo ${config.name}"
  }
}

def git_clone(Map git_clone_config=[:]) {
  checkout(
    [$class: 'GitSCM',
    branches: [[name: '*/master']], 
    doGenerateSubmoduleConfigurations: true, 
    extensions: [], 
    submoduleCfg: [], 
     userRemoteConfigs: [[credentialsId: "$git_clone_config.credential_github_name}", url: 'https://github.com/nafasat/testing_git.git']]])
}


def abc(Map abc_config=[:]) {
  println("frst name is ${abc_config.fname} and last name is ${git_clone_config.lname}")
}
