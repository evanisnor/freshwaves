package com.evanisnor.freshwaves.features.attribution

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.evanisnor.freshwaves.R
import com.evanisnor.freshwaves.databinding.ThirdPartyUsageListFragmentBinding
import com.evanisnor.freshwaves.features.attribution.adapter.OnLicenseSelectedListener
import com.evanisnor.freshwaves.features.attribution.adapter.ThirdPartyUsageListAdapter
import com.evanisnor.freshwaves.features.attribution.model.License
import com.evanisnor.freshwaves.features.attribution.model.LicenseReferenceType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ThirdPartyUsageListFragment : Fragment(), OnLicenseSelectedListener {

  companion object {
    const val TAG = "ThirdPartyUsageListFragment"
  }

  @Inject
  lateinit var attributionRepository: AttributionRepository

  private var binding: ThirdPartyUsageListFragmentBinding? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    return ThirdPartyUsageListFragmentBinding.inflate(inflater, container, false)
      .apply {
        binding = this
      }.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding?.apply {
      toolbar.setNavigationOnClickListener {
        activity?.onBackPressedDispatcher?.onBackPressed()
      }

      thirdPartyUsageList.apply {
        adapter = ThirdPartyUsageListAdapter(
          attributionRepository.getAttributionList(),
          this@ThirdPartyUsageListFragment,
        )
        layoutManager = LinearLayoutManager(context)
      }
    }
  }

  override fun onSelected(license: License) {
    when (license.refType) {
      LicenseReferenceType.URL -> launchBrowser(license.source)
      LicenseReferenceType.EMBEDDED -> launchLicenseViewer(license.source)
    }
  }

  private fun launchBrowser(source: String) {
    startActivity(
      Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(source)
      },
    )
  }

  private fun launchLicenseViewer(source: String) {
    val licenseViewerFragment = LicenseViewerFragment.create(source)

    activity?.supportFragmentManager?.apply {
      beginTransaction()
        .replace(android.R.id.content, licenseViewerFragment)
        .addToBackStack(LicenseViewerFragment.TAG)
        .commit()
    }
  }
}
