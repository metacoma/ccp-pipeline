import jenkins.model.*
import hudson.model.*
import hudson.slaves.*
import hudson.plugins.sshslaves.*
import java.util.ArrayList;
import hudson.slaves.EnvironmentVariablesNodeProperty.Entry;

def publicKey="ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDt52BavE41oHG25pIZyrbjB+o8ipuceNI5fd7DkdH5e/XOO6EfScRhkmavkFJvLB6Lf1HOzL2bR6ApP9JvCZ69ZiXueGLrQHImJVjBPFy9S7k7u4eEC5I1rSdfu8pE4QgmbR5XFjLd7Ox9X3yLg5FkxU8r9mpE9QquPVHHfhDMsEaa2miDVHylocXglqNakAuHmogva/PP+mRs4XLiAG4o7z94B9jvqADU549/SuYUpgZ5uV4M8n1CXmBHT8vIaQXYYt2l5nKxHTNtZSztJ9eOqlOHxN4f5O88z6zeVp33/EPeH2oyy/CF4r4QegrHW56OODRXlM3b2bNrQf93Tsjf"

def randomPort() {
    // TODO generate random port and check its not busy via netstat -nlp
    return 4423
}

def slavePort = randomPort()

def addSlave(name, host, port, publicKey) {
  List<Entry> env = new ArrayList<Entry>();
  env.add(new Entry("key1","value1"))
  env.add(new Entry("key2","value2"))
  EnvironmentVariablesNodeProperty envPro = new EnvironmentVariablesNodeProperty(env);
  Slave slave = new DumbSlave(
                    name,"Agent node description",
                    "/home/jenkins",
                    "1",
                    Node.Mode.NORMAL,
                    "agent-node-label",
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
    addSlave("docker-ccp-build2", '172.16.180.109', slavePort, publicKey)
}
