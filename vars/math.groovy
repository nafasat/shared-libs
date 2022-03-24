def add(x,y) {
  echo "Sum of ${x} and ${y} is ${x+y}"
}

def hostname() {
  sh '''hostname -f'''
}
