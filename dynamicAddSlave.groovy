import jenkins.model.*
import hudson.model.*
import hudson.slaves.*
import hudson.plugins.sshslaves.*
import java.util.ArrayList;
import hudson.slaves.EnvironmentVariablesNodeProperty.Entry;

def publicKey="ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDt52BavE41oHG25pIZyrbjB+o8ipuceNI5fd7DkdH5e/XOO6EfScRhkmavkFJvLB6Lf1HOzL2bR6ApP9JvCZ69ZiXueGLrQHImJVjBPFy9S7k7u4eEC5I1rSdfu8pE4QgmbR5XFjLd7Ox9X3yLg5FkxU8r9mpE9QquPVHHfhDMsEaa2miDVHylocXglqNakAuHmogva/PP+mRs4XLiAG4o7z94B9jvqADU549/SuYUpgZ5uV4M8n1CXmBHT8vIaQXYYt2l5nKxHTNtZSztJ9eOqlOHxN4f5O88z6zeVp33/EPeH2oyy/CF4r4QegrHW56OODRXlM3b2bNrQf93Tsjf"

Random random = new Random();
def randomPort() {
    
    def minPort = 1025
    def maxPort = 65534
    /* TODO check port is free */
    return random.nextInt(maxPort - minPort) + minPort;
}

def slavePort = randomPort()

def addSlave(name, host, port, publicKey) {
  List<Entry> env = new ArrayList<Entry>();
  env.add(new Entry("host", host))
  env.add(new Entry("port", port))
  env.add(new Entry("name", name))
  EnvironmentVariablesNodeProperty envPro = new EnvironmentVariablesNodeProperty(env);
  Slave slave = new DumbSlave(
                    name,"Slave for build ccp images",
                    "/home/jenkins",
                    "1",
                    Node.Mode.NORMAL,
                    "docker-slave-ccp-build",
                    new SSHLauncher(host,port,"jenkins","",publicKey,"","","",""),
                    new RetentionStrategy.Always(),
                    new LinkedList())
  slave.getNodeProperties().add(envPro)
  Jenkins.instance.addNode(slave)
}



node('baremetal') {
    stage 'Pull jenkins slave container'
    sh '''
        docker pull jenkinsci/ssh-slave
    '''
    stage 'Run Container'
    sh """
        docker run -d -p0.0.0.0:$slavePort:22 jenkinsci/ssh-slave "$publicKey"
    """
    stage 'Create slave in jenkins'
    // TODO replace hardcoded ip addr
    // TODO more unique in slave name
    addSlave("docker-ccp-build" . slavePort, '172.16.180.109', slavePort, publicKey)
}
