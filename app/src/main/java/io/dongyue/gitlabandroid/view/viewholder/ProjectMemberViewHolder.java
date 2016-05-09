package io.dongyue.gitlabandroid.view.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.dongyue.gitlabandroid.R;
import io.dongyue.gitlabandroid.model.api.Member;
import io.dongyue.gitlabandroid.network.GitlabClient;
import io.dongyue.gitlabandroid.utils.ImageUtil;

/**
 * Shows a project member
 */
public class ProjectMemberViewHolder extends RecyclerView.ViewHolder{

    public static ProjectMemberViewHolder inflate(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member_project, parent, false);
        return new ProjectMemberViewHolder(view);
    }

    @Bind(R.id.overflow) View mOverflowView;
    @Bind(R.id.name) TextView mUsernameView;
    @Bind(R.id.access) TextView mAccessView;
    @Bind(R.id.image) public ImageView mImageView;

    public final PopupMenu mPopupMenu;

    public ProjectMemberViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);

        mPopupMenu = new PopupMenu(itemView.getContext(), mOverflowView);
        mPopupMenu.getMenuInflater().inflate(R.menu.item_menu_project_member, mPopupMenu.getMenu());

        mOverflowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupMenu.show();
            }
        });
    }

    public void bind(Member member) {
        mUsernameView.setText(member.getUsername());
        mAccessView.setText(Member.getAccessLevel(member.getAccessLevel()));

        GitlabClient.getPicasso()
                .load(ImageUtil.getAvatarUrl(member, itemView.getResources().getDimensionPixelSize(R.dimen.user_header_image_size)))
                .into(mImageView);
    }
}
