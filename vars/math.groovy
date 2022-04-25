def check_val(check_conf=[:]) {
  if ("${config.name}".toUpperCase().equals('yes'.toUpperCase())) {
    println("${config.name}".toUpperCase())
    input_val = '--wait'
  }
}
def custom_fun(Map config=[:]) {
  def name = 'Nafasat'
  def input_val = ''
  if (config.containsKey('name')) {
    check_val(name:'yes')
  } else {
    println("your fist name is ${name} and lname is ${config.lname} and age is ${config.age}")
    input_val = ''
  }
  
  println("helm uninstall ${input_val}")
  
}
def add(x,y) {
  echo "Sum of ${x} and ${y} is ${x+y}"
}

def hostname() {
  sh '''hostname -f'''
}

def count(Map config=[:]) {
  sh "echo $config"
  println(config.keySet())
  println(config.size())
  Adict = ["Mon":3, "Tue":11,"Wed":6,"Thu":9]
  println(Adict.keySet())
  if (config.keySet() >= ["fname", "lname"]) {
    println("Matched")
  } else {
    println("Not Matched")
  }
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
  println("frst name is ${abc_config.fname} and last name is ${abc_config.lname}")
}

abc()
