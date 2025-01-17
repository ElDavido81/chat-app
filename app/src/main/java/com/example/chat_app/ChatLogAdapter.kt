package com.example.chat_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class ChatLogAdapter(var chats: MutableList<Chat>, val onClick: (String) -> Unit) : RecyclerView.Adapter<ChatLogAdapter.ChatLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatLogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chats_list_item, parent, false)
        return ChatLogViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: ChatLogViewHolder, position: Int) {
        holder.chatsTextView.text = chats[position].chatName.toString()
        holder.newMessageDot.visibility = if (chats[position].messages?.isNotEmpty() == true) View.VISIBLE else View.GONE
        holder.chatItem.setOnClickListener {
            onClick(chats[position].chatId.toString())
        }
    }


    inner class ChatLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatsTextView: TextView = itemView.findViewById(R.id.tv_chat)
        val newMessageDot: ImageView = itemView.findViewById(R.id.iv_newmessage)
        val chatItem: ConstraintLayout = itemView.findViewById(R.id.chatItem)
    }
}