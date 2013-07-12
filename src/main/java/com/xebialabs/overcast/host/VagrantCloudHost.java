package com.xebialabs.overcast.host;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xebialabs.overcast.command.CommandResponse;
import com.xebialabs.overcast.support.vagrant.VagrantDriver;
import com.xebialabs.overcast.support.vagrant.VagrantState;

import static com.xebialabs.overcast.support.vagrant.VagrantState.NOT_CREATED;
import static com.xebialabs.overcast.support.vagrant.VagrantState.POWEROFF;
import static com.xebialabs.overcast.support.vagrant.VagrantState.getTransitionCommand;

public class VagrantCloudHost implements CloudHost {

    protected String vagrantIp;

    protected String vagrantVm;

    protected VagrantDriver vagrantDriver;

    private VagrantState initialState;

    private static Logger logger = LoggerFactory.getLogger(VagrantCloudHost.class);

    public VagrantCloudHost(String vagrantVm, String vagrantIp, VagrantDriver vagrantDriver) {
        this.vagrantIp = vagrantIp;
        this.vagrantDriver = vagrantDriver;
        this.vagrantVm = vagrantVm;
    }

    @Override
    public void setup() {
        initialState = vagrantDriver.state(vagrantVm);
        logger.info("Vagrant host is in state {}.", initialState.toString());
        vagrantDriver.doVagrant(vagrantVm, getTransitionCommand(VagrantState.RUNNING));
    }

    @Override
    public void teardown() {
        VagrantState nextState;
        if (initialState != null) {
            logger.info("Bringing vagrant back to {} state.", initialState.toString());
            nextState = initialState;
        } else {
            logger.warn("No initial state was captured. Destroying the VM.");
            nextState = NOT_CREATED;
        }
        vagrantDriver.doVagrant(vagrantVm, getTransitionCommand(nextState));
    }

    @Override
    public String getHostName() {
        return vagrantIp;
    }

    @Override
    public int getPort(int port) {
        return port;
    }

}
