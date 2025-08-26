import api from "../../services/api";

// pages/prepay/prepay.ts
Page({

  /**
   * 页面的初始数据
   */
  data: {
    selectedAmount: null as number | null, // 当前选中的金额
    // 也可以将充值选项定义在这里，如果选项是动态的
    // amountOptions: [
    //   { value: 50, label: '¥50' },
    //   { value: 100, label: '¥100' },
    // ],
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad() {
    // 页面加载时可以进行一些初始化操作
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {
    // 页面初次渲染完成
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    // 页面显示
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {
    // 页面隐藏
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    // 页面卸载
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  },

  // 选择金额
  selectAmount(event: WechatMiniprogram.TouchEvent) {
    const amount = event.currentTarget.dataset.amount as number;
    this.setData({
      selectedAmount: amount,
    });
  },


 // 充值按钮
  recharge() {

    if (!this.data.selectedAmount) {
      wx.showToast({
        title: '请选择充值金额',
        icon: 'none',
      });
      return;
    }

    wx.showModal({
      title: '充值确认',
      content: `确定充值${this.data.selectedAmount}元吗？`,
      success: (res) => {
        if (res.confirm) {
          this.handlePrepay();
        }
      }
    });
  },



  // 处理充值操作
  handlePrepay() {
    if (!this.data.selectedAmount) {
      wx.showToast({
        title: '请选择充值金额',
        icon: 'none',
      });
      return;
    }

    const amountToPrepay = this.data.selectedAmount;
    console.log('发起充值，金额：', amountToPrepay);

    wx.showLoading({ title: '系统处理中...' });
    api.createPayment(amountToPrepay).then((res: any) => {
      wx.hideLoading();
      console.log('预支付订单创建成功', res);

      try {
        // 解析微信支付参数
        const payParams = JSON.parse(res);

        // 检查是否是模拟支付返回
        if (payParams.mock === true) {
          // 模拟支付成功，直接显示成功提示
          wx.showToast({
            title: '充值成功',
            icon: 'success',
            duration: 2000,
            success: () => {
              setTimeout(() => {
                wx.navigateBack();
              }, 2000);
            }
          });
          return;
        }

        // 实际场景
        try {
          // 拉起微信支付
          wx.requestPayment({
            timeStamp: payParams.timeStamp,
            nonceStr: payParams.nonceStr,
            package: payParams.package,
            signType: payParams.signType,
            paySign: payParams.paySign,
            success: () => {
              // 支付成功
              wx.showToast({
                title: '充值成功',
                icon: 'success',
                duration: 2000,
                success: () => {
                  setTimeout(() => {
                    wx.navigateBack();
                  }, 2000);
                }
              });
            },
            fail: (err: any) => {
              // 支付失败或取消
              console.log('支付失败', err);
              if (err.errMsg.indexOf('cancel') > -1) {
                wx.showToast({
                  title: '支付已取消',
                  icon: 'none'
                });
              } else {
                wx.showToast({
                  title: '支付失败，请重试',
                  icon: 'none'
                });
              }
            }
          });
        } catch (parseErr) {
          console.error('解析支付参数失败', parseErr);
          wx.showToast({
            title: '支付参数错误',
            icon: 'none'
          });
        }
      } catch (e) {
        // 解析失败，按正常支付处理
        console.log('非模拟支付模式', e);
      }
      
    }).catch((err: any) => {
      wx.hideLoading();
      console.log('预支付订单创建失败', err);
      wx.showToast({
        title: '网络异常，请重试',
        icon: 'none'
      });
    });
  },
})