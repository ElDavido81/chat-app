package com.example.chat_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessagesAdapter(var messages: MutableList<Message>) : RecyclerView.Adapter<MessagesAdapter.ChatViewHolder>()  {


    // Creates a new 'row' (ViewHolder) in our recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ChatViewHolder(view)
    }

    // Returns a number of objects in the list
    override fun getItemCount(): Int {
        return messages.size
    }

    // Binds data to each ViewHolder. Each element's position in the list has the same position in the recyclerview
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val date = messages[position].timestamp.toDate()

        val simpleDate = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val formattedTime = simpleDate.format(date)

        holder.messageTextView.text = messages[position].message
        holder.timestampTextView.text = formattedTime
        holder.senderNameTextView.text = messages[position].senderName
    }


    // A ViewHolder contains references to views in each 'row'
    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.tv_message)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
        val senderNameTextView: TextView = itemView.findViewById(R.id.senderName_tv)
    }
}