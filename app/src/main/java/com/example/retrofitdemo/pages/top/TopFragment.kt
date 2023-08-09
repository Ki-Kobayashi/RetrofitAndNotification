package com.example.retrofitdemo.pages.top

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.retrofitdemo.R
import com.example.retrofitdemo.databinding.FragmentTopBinding
import com.example.retrofitdemo.domain.repository.AlbumUIState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Created by K.Kobayashi on 2023/07/06.
 */
@AndroidEntryPoint
class TopFragment : Fragment(R.layout.fragment_top) {
    private val vm: TopViewModel by viewModels()

    private var _binding: FragmentTopBinding? = null
    private val binding: FragmentTopBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTopBinding.bind(view)
        initViews(view)
    }

    @SuppressLint("SetTextI18n")
    private fun initViews(view: View) {
        binding.buttonAll.setOnClickListener {
            vm.getAlbums()
        }
        binding.buttonUserId.setOnClickListener {
            vm.getAlbumListByUserId(2)
        }
        binding.buttonUUID.setOnClickListener {
            vm.getAlbumById(3)
        }
        binding.buttonUser.setOnClickListener {
            vm.getUserInfo(3)
        }

        vm.albums
            .onEach {
                if (it is AlbumUIState.Success) {
                    binding.textViewResult.text = null
                    it.albums?.forEach { item ->
                        binding.textViewResult.append("id:${item.id}\ntitle:${item.title}\nuserId:${item.userId}\n\n\n")
                    }
                }
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(lifecycleScope)

        vm.albumState
            .onEach {
                it?.run {
                    binding.textViewResult.text = null
                    binding.textViewResult.text =
                        "id:${it.id}\ntitle:${it.title}\nuserId:${it.userId}\n\n\n"
                }
            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(lifecycleScope)

        vm.userInfo
            .onEach {
                binding.textViewResult.text = null
                binding.textViewResult.text = "userID: ${it?.userId}\n" +
                        "nickname: ${it?.nickname}\n" +
                        "苗字: ${it?.lastName}\n" +
                        "名: ${it?.firstName}\n" +
                        "年齢: ${it?.age}\n" +
                        "説明: ${it?.description}\n" +
                        "削除フラグ: ${it?.deleteFlg}\n" +
                        "作成日: ${it?.createdAt}\n" +
                        "更新日: ${it?.updatedAt}\n"

            }
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .launchIn(lifecycleScope)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
