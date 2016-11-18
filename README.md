```groovy
node('172.16.180.109') {
    git url: 'https://github.com/metacoma/ccp-pipeline.git'
    load('devops.groovy')
    load('ccp.groovy')
    
    kEnv = new KargoEnv(this, 'billy')
    kEnv.setCount(3)
    kEnv.setUbuntuUrl('http://172.16.180.122/images/ubuntu-1604-server-34.qcow2')
    kEnv.deploy() 
    
    ccp = new CcpEnv(this, kEnv)
    
    //FIXME: not implemented
    //ccp.setConfig() 
    ccp.buildImages()
    ccp.deploy()
}
```
