// Generated by view binder compiler. Do not edit!
package com.example.farmmate1.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.farmmate1.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentPlantBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final Button plantAddBtn;

  @NonNull
  public final Button plantListBtnEdit;

  @NonNull
  public final ConstraintLayout plantListLayoutTitle;

  @NonNull
  public final ListView plantListLvPlants;

  @NonNull
  public final TextView plantTitleTv;

  private FragmentPlantBinding(@NonNull FrameLayout rootView, @NonNull Button plantAddBtn,
      @NonNull Button plantListBtnEdit, @NonNull ConstraintLayout plantListLayoutTitle,
      @NonNull ListView plantListLvPlants, @NonNull TextView plantTitleTv) {
    this.rootView = rootView;
    this.plantAddBtn = plantAddBtn;
    this.plantListBtnEdit = plantListBtnEdit;
    this.plantListLayoutTitle = plantListLayoutTitle;
    this.plantListLvPlants = plantListLvPlants;
    this.plantTitleTv = plantTitleTv;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentPlantBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentPlantBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_plant, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentPlantBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.plant_add_btn;
      Button plantAddBtn = ViewBindings.findChildViewById(rootView, id);
      if (plantAddBtn == null) {
        break missingId;
      }

      id = R.id.plant_list_btn_edit;
      Button plantListBtnEdit = ViewBindings.findChildViewById(rootView, id);
      if (plantListBtnEdit == null) {
        break missingId;
      }

      id = R.id.plant_list_layout_title;
      ConstraintLayout plantListLayoutTitle = ViewBindings.findChildViewById(rootView, id);
      if (plantListLayoutTitle == null) {
        break missingId;
      }

      id = R.id.plant_list_lv_plants;
      ListView plantListLvPlants = ViewBindings.findChildViewById(rootView, id);
      if (plantListLvPlants == null) {
        break missingId;
      }

      id = R.id.plant_title_tv;
      TextView plantTitleTv = ViewBindings.findChildViewById(rootView, id);
      if (plantTitleTv == null) {
        break missingId;
      }

      return new FragmentPlantBinding((FrameLayout) rootView, plantAddBtn, plantListBtnEdit,
          plantListLayoutTitle, plantListLvPlants, plantTitleTv);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
