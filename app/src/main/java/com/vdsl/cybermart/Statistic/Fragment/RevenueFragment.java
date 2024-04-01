package com.vdsl.cybermart.Statistic.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.Order.Model.Order;
import com.vdsl.cybermart.databinding.FragmentRevenueBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class RevenueFragment extends Fragment {
    FragmentRevenueBinding binding;
    ArrayList<BarEntry> revenueList;
    ArrayList<String> months;
    HashSet<Integer> years;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRevenueBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseDatabase.getInstance().getReference("Orders")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        years = new HashSet<>();
                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                            String dateString = orderSnapshot.child("date").getValue(String.class);
                            String[] parts;
                            if (dateString != null) {
                                parts = dateString.split("/");
                                for (String y: parts){
                                    if (y.length() >= 4) {
                                        years.add(Integer.parseInt(y));
                                    }
                                }
                            }

                        }
                        Log.d("TAG", "set size: " + years.size());
                        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>(years));
                        binding.spYear.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("TAG", "Failed to read value.", error.toException());
                    }
                });

        binding.spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int yearArg = (int) binding.spYear.getSelectedItem();
                getRevenue(yearArg);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void updateUI(int month, float ch) {
        months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.valueOf(i));
        }
        revenueList.add(new BarEntry(month, ch));
        binding.revenueChart.getAxisRight().setDrawLabels(false);
        YAxis yAxis = binding.revenueChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(500f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);
        BarDataSet barDataSet = new BarDataSet(revenueList, "Revenue");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.GREEN);
        BarData barData = new BarData(barDataSet);
        binding.revenueChart.setData(barData);
        binding.revenueChart.getDescription().setEnabled(false);
        binding.revenueChart.invalidate();
        binding.revenueChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(months));
        binding.revenueChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.revenueChart.getXAxis().setGranularity(1f);
        binding.revenueChart.getXAxis().setGranularityEnabled(true);
    }

    @SuppressLint("DefaultLocale")
    private void getRevenue(int year) {
        revenueList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            final float[] totalInMonth = {0};
            int[] total = {0};
            String startDate = String.format("%04d/%02d/01", year, i);
            String endDate = String.format("%04d/%02d/31", year, i);
            Query query = FirebaseDatabase.getInstance().getReference("Orders")
                    .orderByChild("date").startAt(startDate).endAt(endDate);
            int finalI = i;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        Order order = dataSnapshot.getValue(Order.class);
                        total[0] = (int) order.getCartModel().getTotalPrice();
                        DataSnapshot totalSnapshot = dataSnapshot.child("total");
                        if (totalSnapshot.exists()) {
                            float totalData = Objects.requireNonNull(totalSnapshot.getValue(Float.class));
                            Log.d("TAG", "onDataChange: " + totalData);
                            totalInMonth[0] += totalData;
                        } else {
                            Log.d("TAG", "onDataChange: " + totalSnapshot);
                        }
                    }
                    //do bất đồng bộ nên cần phải thêm vào list ở hàm khác
                    updateUI(finalI, totalInMonth[0]);
                    Log.d("TAG", "getRevenue: " + Arrays.toString(total) + " " + Arrays.toString(totalInMonth));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("TAG", "onCancelled: Error");
                }
            });
        }
    }
}