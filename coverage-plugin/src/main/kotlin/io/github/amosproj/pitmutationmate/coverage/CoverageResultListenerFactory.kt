// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: 2023

package io.github.amosproj.pitmutationmate.coverage

import org.pitest.mutationtest.ListenerArguments
import org.pitest.mutationtest.MutationResultListener
import org.pitest.mutationtest.MutationResultListenerFactory
import org.pitest.mutationtest.report.html.HtmlReportFactory
import java.util.*

class CoverageResultListenerFactory : MutationResultListenerFactory {
    override fun description(): String {
        return "Coverage Report writer for PIT"
    }

    override fun getListener(props: Properties?, args: ListenerArguments?): MutationResultListener {
        val htmlListener = HtmlReportFactory().getListener(props, args)
        if (args != null) {
            return CoverageResultListener(htmlListener, args.coverage, args.outputStrategy)
        }
        return CoverageResultListener(htmlListener, null, null)
    }

    override fun name(): String {
        return "report-coverage"
    }
}
