package com.amos.pitmutationmate.pitmutationmate.configuration

import com.intellij.execution.RunManager
import com.intellij.execution.configuration.RunConfigurationExtensionBase
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunnerSettings

class MutationMateRunConfigurationExtension(runConfiguration: RunConfiguration) :
        RunConfigurationExtensionBase<MutationMateRunConfiguration>() {

    override fun isApplicableFor(configuration: MutationMateRunConfiguration): Boolean {
        return true
    }

    override fun isEnabledFor(
        applicableConfiguration: MutationMateRunConfiguration,
        runnerSettings: RunnerSettings?
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun patchCommandLine(
        configuration: MutationMateRunConfiguration,
        runnerSettings: RunnerSettings?,
        cmdLine: GeneralCommandLine,
        runnerId: String
    ) {
        TODO("Not yet implemented")
    }

    fun setClassName(className: String) {
        val runConfigs = (MutationMateRunConfigurationType::class.java)
        runConfigs.first() .className = className
    }
}