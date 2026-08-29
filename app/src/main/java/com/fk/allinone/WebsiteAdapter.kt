package com.fk.allinone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder


class WebsiteAdapter(
    private val websites: MutableList<Website>,
    private val onWebsiteClicked: (Website) -> Unit,
    private val onWebsiteDeleted: (Website) -> Unit
) : RecyclerView.Adapter<WebsiteAdapter.WebsiteViewHolder>() {


    // ================================================================
    // VIEW HOLDER
    // ================================================================

    class WebsiteViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val siteIcon: ImageView =
            itemView.findViewById(R.id.siteIcon)

        val siteName: TextView =
            itemView.findViewById(R.id.siteName)

        val deleteButton: ImageButton =
            itemView.findViewById(R.id.deleteButton)


        // ------------------------------------------------------------
        // DELETE BUTTON AUTO-HIDE
        // ------------------------------------------------------------

        val hideDeleteButtonRunnable =
            Runnable {

                deleteButton.animate()
                    .alpha(0f)
                    .setDuration(150)
                    .withEndAction {

                        deleteButton.visibility =
                            View.GONE

                        deleteButton.alpha =
                            1f
                    }
                    .start()
            }
    }


    // ================================================================
    // CREATE VIEW HOLDER
    // ================================================================

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WebsiteViewHolder {

        val view =
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_website,
                    parent,
                    false
                )

        return WebsiteViewHolder(
            view
        )
    }


    // ================================================================
    // BIND VIEW HOLDER
    // ================================================================

    override fun onBindViewHolder(
        holder: WebsiteViewHolder,
        position: Int
    ) {

        val website =
            websites[position]


        // ------------------------------------------------------------
        // RESET VIEW STATE
        // ------------------------------------------------------------

        holder.deleteButton.removeCallbacks(
            holder.hideDeleteButtonRunnable
        )

        holder.deleteButton.animate().cancel()

        holder.deleteButton.visibility =
            View.GONE

        holder.deleteButton.alpha =
            1f


        // ------------------------------------------------------------
        // WEBSITE NAME
        // ------------------------------------------------------------

        holder.siteName.text =
            website.name


        // ------------------------------------------------------------
        // WEBSITE ICON
        // ------------------------------------------------------------

        if (
            !website.iconUrl.isNullOrBlank()
        ) {

            holder.siteIcon.load(
                website.iconUrl
            ) {

                crossfade(true)

                placeholder(
                    R.drawable.ic_default_site
                )

                error(
                    R.drawable.ic_default_site
                )
            }

        } else {

            holder.siteIcon.setImageResource(
                R.drawable.ic_default_site
            )
        }


        // ------------------------------------------------------------
        // CARD CLICK
        // ------------------------------------------------------------

        holder.itemView.setOnClickListener {

            if (
                holder.deleteButton.visibility ==
                View.VISIBLE
            ) {

                hideDeleteButton(
                    holder
                )

            } else {

                onWebsiteClicked(
                    website
                )
            }
        }


        // ------------------------------------------------------------
        // LONG PRESS
        // ------------------------------------------------------------

        holder.itemView.setOnLongClickListener {

            // Cancel previous timer
            holder.deleteButton.removeCallbacks(
                holder.hideDeleteButtonRunnable
            )


            // Cancel previous animation
            holder.deleteButton.animate().cancel()


            // Show delete button
            holder.deleteButton.visibility =
                View.VISIBLE

            holder.deleteButton.alpha =
                0f


            // Fade in
            holder.deleteButton.animate()
                .alpha(1f)
                .setDuration(150)
                .start()


            // Automatically hide after 3 seconds
            holder.deleteButton.postDelayed(
                holder.hideDeleteButtonRunnable,
                3000L
            )


            // Consume long press
            true
        }


        // ------------------------------------------------------------
        // DELETE BUTTON
        // ------------------------------------------------------------

        holder.deleteButton.setOnClickListener {

            val adapterPosition =
                holder.bindingAdapterPosition


            if (
                adapterPosition ==
                RecyclerView.NO_POSITION
            ) {

                return@setOnClickListener
            }


            // Get the website before removing it
            val deletedWebsite =
                websites[adapterPosition]


            // Cancel auto-hide timer
            holder.deleteButton.removeCallbacks(
                holder.hideDeleteButtonRunnable
            )


            // Cancel animation
            holder.deleteButton.animate().cancel()


            // Remove from list
            websites.removeAt(
                adapterPosition
            )


            // Update RecyclerView
            notifyItemRemoved(
                adapterPosition
            )


            // Tell MainActivity to persist deletion
            onWebsiteDeleted(
                deletedWebsite
            )
        }
    }


    // ================================================================
    // HIDE DELETE BUTTON
    // ================================================================

    private fun hideDeleteButton(
        holder: WebsiteViewHolder
    ) {

        holder.deleteButton.removeCallbacks(
            holder.hideDeleteButtonRunnable
        )


        holder.deleteButton.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {

                holder.deleteButton.visibility =
                    View.GONE

                holder.deleteButton.alpha =
                    1f
            }
            .start()
    }


    // ================================================================
    // VIEW RECYCLED
    // ================================================================

    override fun onViewRecycled(
        holder: WebsiteViewHolder
    ) {

        holder.deleteButton.removeCallbacks(
            holder.hideDeleteButtonRunnable
        )

        holder.deleteButton.animate().cancel()


        holder.deleteButton.visibility =
            View.GONE

        holder.deleteButton.alpha =
            1f


        super.onViewRecycled(
            holder
        )
    }


    // ================================================================
    // ITEM COUNT
    // ================================================================

    override fun getItemCount(): Int {

        return websites.size
    }
}