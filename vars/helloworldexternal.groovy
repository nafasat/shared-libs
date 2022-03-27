def call(Map config = [:]) {
  loadlinuxScript(name: 'helloworld.sh')
  sh "./helloworld.sh ${config.first} ${config.second}"
}
