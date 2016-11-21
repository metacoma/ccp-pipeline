class VirtualEnv implements Serializable  {
  private String path;
  private ctx;

  @NonCPS
  def _init() {
    this.ctx.sh "test -d " + this.path + "|| virtualenv " + this.path
  }

  @NonCPS
  def run(cmd) {
    String venvCmd = ". " + this.path + "/bin/activate && " + cmd
    this.ctx.echo venvCmd
    this.ctx.sh venvCmd
  }

  def install(requirement) {
    this.run 'pip install ' + requirement
  }

  def VirtualEnv(ctx, path) {
    this.ctx = ctx
    this.path = path
  }

  def getPath() {
    return this.path
  }

}

def installDevops(ctx) {
    def devopsEnv = new VirtualEnv(ctx, ".fuel-devops-venv")
    devopsEnv._init()
    devopsEnv.install('psycopg2')
    devopsEnv.install('git+https://github.com/openstack/fuel-devops.git@3.0.1 --upgrade')
    return devopsEnv
}

def installCcp(ctx) {
    stage('Install fuel-ccp') {
        git url: 'https://git.openstack.org/openstack/fuel-ccp'
        sh '''
            sudo pip install ./
        '''
    }
}

env.GERRIT_EVENT_TYPE='patchset-created'
env.GERRIT_PROJECT="fuel-ccp-etcd"
env.GERRIT_REFSPEC="refspec/01/001/1"

node('env2.master') {
    assert env.GERRIT_PROJECT 
    installCcp(this)
    try { 
      switch(env.GERRIT_EVENT_TYPE) {
            case 'patchset-created':
                assert(env.GERRIT_REFSPEC)
                stage("Repo checkout") {
                    sh '''
                        rm -rf $HOME/ccp-repos
                        ccp fetch
                    '''
                }
                stage('Apply patchset') {
                    sh '''
                        cd $HOME/ccp-repos/$GERRIT_PROJECT
                        #git fetch origin $GERRIT_REFSPEC
                        #git checkout FETCH_HEAD
                        
                    '''
                }
                stage('Build image') {
                    sh '''
                       ccp build -c `echo ${GERRIT_PROJECT} | awk -F- '{print $NF}'`
                    '''
                }
                break
            case 'change-merged':
                /* not implemented */
                break
            default:
                error("Unsupported event type " . env_GERRIT_EVENT_TYPE)
      
      }
    
  }
  catch(err) {
    echo "Failed: ${err}"
    currentBuild.result = 'FAILURE'
  }
  finally {
    // fix workspace owners
    sh "sudo chown -R $USER:$USER ${WORKSPACE}"
  }

}
