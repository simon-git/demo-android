package chat.dim.sechat.chatbox.ui.chatbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import chat.dim.Content;
import chat.dim.ID;
import chat.dim.InstantMessage;
import chat.dim.model.Conversation;
import chat.dim.model.ConversationDatabase;
import chat.dim.model.Facebook;
import chat.dim.protocol.Command;
import chat.dim.protocol.TextContent;
import chat.dim.sechat.R;

public class MessageArrayAdapter extends ArrayAdapter<InstantMessage> {

    private final int resId;

    Conversation chatBox = null;

    MessageArrayAdapter(Context context, int resource, List<InstantMessage> objects) {
        super(context, resource, objects);
        resId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstantMessage iMsg = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = view.findViewById(R.id.recv_msg);
            viewHolder.centerLayout = view.findViewById(R.id.cmd_msg);
            viewHolder.rightLayout = view.findViewById(R.id.sent_msg);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        assert iMsg != null;
        MsgType type = ChatboxViewModel.getType(iMsg, chatBox);
        if (MsgType.SENT == type) {
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.centerLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.timeView = view.findViewById(R.id.time_text);
            viewHolder.avatarView = view.findViewById(R.id.sent_avatar);
            viewHolder.nameView = view.findViewById(R.id.sent_name);
            viewHolder.msgView = view.findViewById(R.id.sent_text);
        } else if (MsgType.RECEIVED == type) {
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.centerLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.timeView = view.findViewById(R.id.time_text);
            viewHolder.avatarView = view.findViewById(R.id.recv_avatar);
            viewHolder.nameView = view.findViewById(R.id.recv_name);
            viewHolder.msgView = view.findViewById(R.id.recv_text);
        } else if (MsgType.COMMAND == type) {
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.centerLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.timeView = view.findViewById(R.id.time_text);
            viewHolder.avatarView = null;
            viewHolder.nameView = null;
            viewHolder.msgView = view.findViewById(R.id.cmd_text);
        }
        showMessage(iMsg, viewHolder);

        return view;
    }

    private Facebook facebook = Facebook.getInstance();
    private ConversationDatabase msgDB = ConversationDatabase.getInstance();

    private void showMessage(InstantMessage iMsg, ViewHolder viewHolder) {
        ID sender = facebook.getID(iMsg.envelope.sender);
        Content content = iMsg.content;

        // time
        String time = msgDB.getTimeString(iMsg);
        if (time == null) {
            viewHolder.timeView.setVisibility(View.GONE);
        } else {
            viewHolder.timeView.setVisibility(View.VISIBLE);
            viewHolder.timeView.setText(time);
        }

        // avatar

        // name
        if (viewHolder.nameView != null) {
            String name = facebook.getNickname(sender);
            viewHolder.nameView.setText(name);
        }

        // message
        if (content instanceof TextContent) {
            TextContent textContent = (TextContent) content;
            viewHolder.msgView.setText(textContent.getText());
        } else if (content instanceof Command) {
            Command cmd = (Command) content;
            String text = msgDB.getCommandText(cmd, sender);
            viewHolder.msgView.setText(text);
        }
    }

    class ViewHolder {
        LinearLayout leftLayout = null;
        LinearLayout rightLayout = null;
        LinearLayout centerLayout = null;

        TextView timeView = null;
        ImageView avatarView = null;
        TextView nameView = null;
        TextView msgView = null;
    }
}
