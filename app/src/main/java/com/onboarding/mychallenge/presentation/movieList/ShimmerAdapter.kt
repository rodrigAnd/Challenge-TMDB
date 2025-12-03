package com.onboarding.mychallenge.presentation.movieList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.onboarding.mychallenge.databinding.ItemMovieShimmerBinding

/**
 * Adapter para exibir itens de Shimmer durante o carregamento
 */
class ShimmerAdapter(private val itemCount: Int = 5) : RecyclerView.Adapter<ShimmerAdapter.ShimmerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShimmerViewHolder {
        val binding = ItemMovieShimmerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShimmerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShimmerViewHolder, position: Int) {
        holder.startShimmer()
    }

    override fun getItemCount(): Int = itemCount

    override fun onViewDetachedFromWindow(holder: ShimmerViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.stopShimmer()
    }

    class ShimmerViewHolder(
        private val binding: ItemMovieShimmerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun startShimmer() {
            binding.shimmerFrameLayout.startShimmer()
        }

        fun stopShimmer() {
            binding.shimmerFrameLayout.stopShimmer()
        }
    }
}
