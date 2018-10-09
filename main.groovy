#!/usr/bin/env groovy
import org.mulesoft.BetaEnvironments

def betaEnvs = new BetaEnvironments("jorge-nfs-test-4", "1808cc86-43c4-4dcc-a1d3-0fafc96e70cf")
def details = betaEnvs.get_environment_details()
println(details)
