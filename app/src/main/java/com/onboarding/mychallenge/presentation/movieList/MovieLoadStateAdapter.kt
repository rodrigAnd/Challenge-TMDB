package com.onboarding.mychallenge.presentation.movieList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.onboarding.mychallenge.databinding.ItemLoadStateBinding

/**
 * Adapter para exibir estados de loading e erro no RecyclerView usando Paging 3.
 */
class MovieLoadStateAdapter(
    private val retry: () -> Unit,
) : LoadStateAdapter<MovieLoadStateAdapter.LoadStateViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState,
    ): LoadStateViewHolder {
        val binding =
            ItemLoadStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return LoadStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(
        holder: LoadStateViewHolder,
        loadState: LoadState,
    ) {
        holder.bind(loadState)
    }

    class LoadStateViewHolder(
        private val binding: ItemLoadStateBinding,
        private val retry: () -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                retryButton.isVisible = loadState is LoadState.Error
                errorMessage.isVisible = loadState is LoadState.Error

                if (loadState is LoadState.Error) {
                    errorMessage.text = loadState.error.localizedMessage
                        ?: "Erro ao carregar dados"
                }

                retryButton.setOnClickListener {
                    retry()
                }
            }
        }
    }
}
