
package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * 真正的绑定到RecyclerView中的ViewHolder
 * 1.将layoutId 当做 itemViewType (也就是说每个item的layoutId就是这个item的type) 省去了type的自定义
 * 2.包含了 {@link EpoxyModel} 代表每个item的Model,提供item的基本的一些操作(show,状态保存与否等)
 * 3.{@link EpoxyHolder} 提供了对当前item的View绑定,便于将view传递出去找到对应的view
 * 4.{@link EpoxyModelWithHolder} 对普通的 {@link EpoxyModel}提供了bindView的功能 方便使用ViewHolder模式
 */
@SuppressWarnings("WeakerAccess")
public class EpoxyViewHolder extends RecyclerView.ViewHolder {
  @SuppressWarnings("rawtypes") private EpoxyModel epoxyModel;
  private List<Object> payloads;
  private EpoxyHolder epoxyHolder;

  public EpoxyViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
    super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
  }

  /**
   * 在{@link EpoxyAdapter#onBindViewHolder(EpoxyViewHolder, int)}中绑定的时候调用
   */
  public void bind(@SuppressWarnings("rawtypes") EpoxyModel model, List<Object> payloads) {
    this.payloads = payloads;

    if (epoxyHolder == null && model instanceof EpoxyModelWithHolder) {
      epoxyHolder = ((EpoxyModelWithHolder) model).createNewHolder();
      epoxyHolder.bindView(itemView);
    }

    //当使用了 ViewHolder Patten模式,bind的对象就是EpoxyModelWithHolder
    //没有是用EpoxyModelWithHolder的时候 bind的对象是当前itemView,
    //ViewHolder Patten只是通过holder将itemView包装一下返回对应的View而已
    if (payloads.isEmpty()) {
      // noinspection unchecked
      model.bind(objectToBind());
    } else {
      // noinspection unchecked
      model.bind(objectToBind(), payloads);
    }

    epoxyModel = model;
  }

  private Object objectToBind() {
    return epoxyHolder != null ? epoxyHolder : itemView;
  }

  /**
   * 在{@link EpoxyAdapter#onViewRecycled(EpoxyViewHolder)} 被回收的时候调用
   */
  public void unbind() {
    assertBound();
    // noinspection unchecked
    epoxyModel.unbind(objectToBind());
    epoxyModel = null;
    payloads = null;
  }

  public List<Object> getPayloads() {
    assertBound();
    return payloads;
  }

  public EpoxyModel<?> getModel() {
    assertBound();
    return epoxyModel;
  }

  private void assertBound() {
    if (epoxyModel == null) {
      throw new IllegalStateException("This holder is not currently bound.");
    }
  }

  @Override
  public String toString() {
    return "EpoxyViewHolder{"
        + "epoxyModel=" + epoxyModel
        + ", view=" + itemView
        + ", super=" + super.toString()
        + '}';
  }
}
