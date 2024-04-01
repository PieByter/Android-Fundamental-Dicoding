package com.example.githubproject.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubproject.adapter.FollowAdapter
import com.example.githubproject.databinding.FragmentFollowBinding
import com.example.githubproject.viewmodel.FollowViewModel

class FollowFragment : Fragment() {

    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FollowViewModel
    private lateinit var followAdapter: FollowAdapter

    private var position: Int = 0
    private var username: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME) ?: ""
        }

        viewModel = FollowViewModel()
        followAdapter = FollowAdapter(requireContext())

        binding.rvFollow.apply {
            adapter = followAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.loadingIndicator.visibility = View.VISIBLE
        viewModel.getFollowersFollowing(position, username)

        viewModel.followersFollowing.observe(viewLifecycleOwner) { followersFollowing ->
            binding.loadingIndicator.visibility = View.GONE
            followersFollowing?.let {
                followAdapter.setData(it)
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_POSITION = "position"
        const val ARG_USERNAME = "username"
    }
}
