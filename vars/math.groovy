def check_val(check_conf=[:]) {
  if ("${check_conf.name}".toUpperCase().equals('yes'.toUpperCase())) {
    println("${check_conf.name}".toUpperCase())
    input_val = '--wait'
  }
}

def custom_fun(Map ssh_config=[:]) {
  if (ssh_config.containsKey('key')) {
    println("echo ssh with key ${ssh_config.key}")  
    ssh_config.each {
      if ("$it.value" == '' || "$it.value" == 'null') {
        println("error $it")
      }
    }
  } else {
    println("echo ssh without key !")
  }
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
  println("${config}")
  
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
