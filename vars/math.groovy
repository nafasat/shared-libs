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
  sh "echo ${config.name}"
}
