package com.evanisnor.freshwaves.features.attribution

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.evanisnor.freshwaves.databinding.ThirdPartyLicenseFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LicenseViewerFragment : Fragment() {

    companion object {
        const val TAG = "LicenseViewerFragment"

        private const val sourceArgument =
            "com.evanisnor.freshwaves.features.attribution.arg.Source"

        fun create(source: String): Fragment = LicenseViewerFragment().apply {
            arguments = Bundle().apply {
                putString(sourceArgument, source)
            }
        }
    }

    @Inject
    lateinit var attributionRepository: AttributionRepository

    private var binding: ThirdPartyLicenseFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ThirdPartyLicenseFragmentBinding.inflate(inflater, container, false)
            .apply {
                binding = this
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(sourceArgument)?.let {
            binding?.licenseText?.text = attributionRepository.getLicenseText(it)
        }
    }


}