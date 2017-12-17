/**
 * @author: haiyangp
 * @date: 2017/12/17
 * desc: 将项目部署到阿里云服务器脚本
 */
node {
    stage('get clone') {
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], gitTool: 'Default', submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/puhaiyang/onlineJavaIde.git']]])
    }

    stage('mvn test') {
        withMaven(maven: 'M3') {
            sh "mvn test"
        }
    }

    stage('mvn install') {
        withMaven(maven: 'M3') {
            sh "mvn install"
        }
    }



    stage('depoly') {
        sh 'sshpass -p  hostpassword ssh  -o StrictHostKeyChecking=no username@hostname ls'
        sh 'sshpass -p  hostpassword scp  -o StrictHostKeyChecking=no  target/*.jar username@hostname:.'
        echo 'haha'
    }
    //    -p:指定ssh的密码
    //    -o StrictHostKeyChecking=no 避免第一次登录出现公钥检查。也就是避免出现


    stage('restart') {
        timeout(time: 1, unit: 'DAYS') {
            input message: 'Approve deployment?', submitter: 'it-ops'
        }
    }

}