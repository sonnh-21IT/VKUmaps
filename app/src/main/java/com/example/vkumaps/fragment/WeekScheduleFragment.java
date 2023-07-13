package com.example.vkumaps.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeekScheduleFragment extends Fragment {
    private CalendarView calendarView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> weekSchedule;
    private final ChangeFragmentListener listener;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public WeekScheduleFragment(ChangeFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_week_schedule, container, false);

        calendarView = rootView.findViewById(R.id.calendarView);
        listView = rootView.findViewById(R.id.listView);

        listener.changeTitle("Lịch học tuần này");

        if (user != null) {
            // Người dùng đã đăng nhập
            showPersonalSchedule();
        } else {
            // Người dùng chưa đăng nhập
            // Hiển thị lịch mặc định và thông báo lỗi
            showPersonalSchedule();
        }

        return rootView;
    }

    private void showPersonalSchedule() {
        // Khởi tạo dữ liệu lịch học
        weekSchedule = new ArrayList<>();
        weekSchedule.add("Monday - Math");
        weekSchedule.add("Tuesday - Science");
        weekSchedule.add("Wednesday - History");
        weekSchedule.add("Thursday - English");
        weekSchedule.add("Friday - Gym");

        // Thiết lập adapter cho ListView
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, weekSchedule);
        listView.setAdapter(adapter);

        // Thiết lập sự kiện lắng nghe cho CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Xử lý sự kiện khi ngày được chọn thay đổi
                updateSchedule(year, month, dayOfMonth);
            }
        });
    }

    private void updateSchedule(int year, int month, int dayOfMonth) {
        // Lấy dữ liệu lịch học cho ngày được chọn
        String selectedDate = getFormattedDate(year, month, dayOfMonth);
        List<String> daySchedule = getScheduleForDay(selectedDate);

        // Cập nhật adapter của ListView với dữ liệu lịch học cho ngày được chọn
        adapter.clear();
        if (daySchedule != null) {
            adapter.addAll(daySchedule);
        }
        adapter.notifyDataSetChanged();
    }

    private String getFormattedDate(int year, int month, int dayOfMonth) {
        // Hàm chuyển đổi ngày, tháng, năm thành chuỗi định dạng
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        return sdf.format(calendar.getTime());
    }

    private List<String> getScheduleForDay(String date) {
        // Trả về lịch học cho ngày được chọn từ dữ liệu lịch học toàn tuần
        // (trong ví dụ này, dữ liệu lịch học được lưu trữ trong weekSchedule)
        // Bạn có thể cung cấp một cơ sở dữ liệu hoặc cách lưu trữ dữ liệu tùy chỉnh khác cho ứng dụng của bạn.
        // Trong ví dụ này, chỉ sử dụng một danh sách đơn giản.
        // Đảm bảo cung cấp các phương thức phù hợp để truy vấn và trả về lịch học tương ứng.
        return weekSchedule;
    }

    private void showDefaultSchedule() {
        Toast.makeText(requireContext(), "Vui lòng đăng nhập để xem lịch học của bạn!", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}