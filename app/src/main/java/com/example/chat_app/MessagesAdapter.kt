package com.example.chat_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessagesAdapter(var messages: MutableList<Message>) : RecyclerView.Adapter<MessagesAdapter.ChatViewHolder>()  {


    // Skapar en ny 'rad' (ViewHolder) i vår recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ChatViewHolder(view)
    }

    // Returnerar antal objekt i listan
    override fun getItemCount(): Int {
        return messages.size
    }

    // Binder data till varje ViewHolder. Varje elements position i listan har samma position i recyclerview
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val date = messages[position].timestamp.toDate()

        val simpleDate = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val formattedTime = simpleDate.format(date)

        holder.messageTextView.text = messages[position].message
        holder.timestampTextView.text = formattedTime
        holder.senderNameTextView.text = messages[position].senderName
    }


    // En ViewHolder innehåller referenserna till vyerna i varje 'rad'
    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.tv_message)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
        val senderNameTextView: TextView = itemView.findViewById(R.id.senderName_tv)
    }
}