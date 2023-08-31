package com.example.vkumaps.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;

public class AboutFragment extends Fragment implements View.OnClickListener {
    private ChangeFragmentListener listener;
    private View rootView;
    private final String CONTACT_PHONE_DT_DT = "02366552688";
    private final String CONTACT_FANPAGE_VKU = "https://www.facebook.com/vku.udn.vn";
    private final String CONTACT_PHONE_DT_TT = "0935048080";
    private final String CONTACT_MAIL_DT_DT = "daotao@vku.udn.vn";

    public AboutFragment(ChangeFragmentListener listener) {
        this.listener = listener;
    }

    public AboutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about, container, false);
        listener.changeTitle("Về chúng tôi");
        // Inflate the layout for this fragment
        initView();
        return rootView;
    }

    private void initView() {
        LinearLayout lnCallDt = rootView.findViewById(R.id.call_pdt);
        LinearLayout lnCallTt = rootView.findViewById(R.id.call_tt);
        LinearLayout lnMailDt = rootView.findViewById(R.id.mail_pdt);
        LinearLayout lnFb = rootView.findViewById(R.id.fb);

        lnMailDt.setOnClickListener(this);
        lnCallDt.setOnClickListener(this);
        lnCallTt.setOnClickListener(this);
        lnFb.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        showPopupMenu(view);
    }

    private void showPopupMenu(View anchorView) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchorView);
        if (anchorView.getId() == R.id.call_pdt || anchorView.getId() == R.id.call_tt) {
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup_contact_sdt, popupMenu.getMenu());
        } else if (anchorView.getId() == R.id.mail_pdt) {
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup_contact_mail, popupMenu.getMenu());
        } else {
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup_contact_link, popupMenu.getMenu());
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String contact = "";
                boolean checkNow = false;
                boolean isMail = false;
                boolean isLink = false;
                switch (item.getItemId()) {
                    case R.id.menu_now_phone:
                        isMail = false;
                        isLink = false;
                        checkNow = true;
                        if (anchorView.getId() == R.id.call_tt) {
                            contact = CONTACT_PHONE_DT_TT;
                        } else if (anchorView.getId() == R.id.call_pdt) {
                            contact = CONTACT_PHONE_DT_DT;
                        }
                        break;
                    case R.id.menu_now_mail:
                        isMail = true;
                        isLink = false;
                        checkNow = true;
                        if (anchorView.getId() == R.id.mail_pdt) {
                            contact = CONTACT_MAIL_DT_DT;
                        }
                        break;
                    case R.id.menu_now_link:
                        isMail = false;
                        isLink = true;
                        checkNow = true;
                        if (anchorView.getId() == R.id.fb) {
                            contact = CONTACT_FANPAGE_VKU;
                        }
                        break;
                    case R.id.menu_copy_mail:
                        isMail = true;
                        isLink = false;
                        checkNow = false;
                        if (anchorView.getId() == R.id.mail_pdt) {
                            contact = CONTACT_MAIL_DT_DT;
                        }
                        break;
                    case R.id.menu_copy_phone:
                        isMail = false;
                        isLink = false;
                        checkNow = false;
                        if (anchorView.getId() == R.id.call_tt) {
                            contact = CONTACT_PHONE_DT_TT;
                        } else if (anchorView.getId() == R.id.call_pdt) {
                            contact = CONTACT_PHONE_DT_TT;
                        }
                        break;
                    case R.id.menu_copy_link:
                        isMail = false;
                        isLink = true;
                        checkNow = false;
                        if (anchorView.getId() == R.id.fb) {
                            contact = CONTACT_FANPAGE_VKU;
                        }
                        break;
                    default:
                        break;
                }
                if (!contact.equals("")) {
                    if (checkNow) {
                        if (isMail) {
                            openEmailComposer(contact);
                        } else if (isLink) {
                            openLink(contact);
                        } else {
                            dialPhoneNumber(contact);
                        }
                    } else {
                        copyInfoContact(contact);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }

    private void openLink(String link) {
        Uri uri = Uri.parse(link);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    private void dialPhoneNumber(String phoneNumber) {
        // Tạo Intent để mở màn hình ghi số điện thoại
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void copyInfoContact(String contact) {
        // Khởi tạo ClipboardManager từ context
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        // Tạo ClipData để chứa nội dung cần copy
        ClipData clipData = ClipData.newPlainText("label", contact);

        // Sao chép ClipData vào clipboard
        clipboardManager.setPrimaryClip(clipData);

        Toast.makeText(requireContext(), "Đã sao chép vào bộ nhớ tạm", Toast.LENGTH_SHORT).show();
    }

    private void openEmailComposer(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + email));

        startActivity(intent);
    }
}