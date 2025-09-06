// 定义 TabBarItem 接口
interface TabBarItem {
  pagePath: string;
  text: string;
  iconPath: string;
  selectedIconPath: string;
  iconBase64?: string;
  selectedIconBase64?: string;
  isSpecial?: boolean;
}

Component({
  data: {
    selected: 0, // 当前选中的 tabBar 项的索引
    color: "#999999", // 未选中的颜色
    selectedColor: "#3478f6", // 选中时的颜色 - 使用更浅的iOS风格蓝色
    list: [
      {
        pagePath: "/pages/index/index",
        text: "首页",
        iconPath: "home",
        selectedIconPath: "home-fill",
        // 添加对应图标的 Base64 编码，作为加载失败时的备选
        iconBase64: "data:image/svg+xml;base64,PHN2ZyB2aWV3Qm94PSIwIDAgMTAyNCAxMDI0IiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIj48cGF0aCBkPSJNOTYwIDU2OC41NTRjLTguNzYyIDAtMTcuNTIzLTMuMzItMjQuMi0xMC4wODJMNTEyIDEzNC41ODQgODguMiA1NTguNTU2Yy0xMy4zMTggMTMuNDAyLTM0Ljk1NyAxMy40MDItNDguMzYgMC0xMy40MDItMTMuMzE4LTEzLjQwMi0zNC45NTcgMC00OC4zNmw0NDcuOS00NDguNzg0YzEzLjMxOC0xMy40MDIgMzQuOTU3LTEzLjQwMiA0OC4zNiAwTDk4NC4yIDUxMC4xMTJjMTMuNDAyIDEzLjMxOCAxMy40MDIgMzQuOTU3IDAgNDguMzYtNi42NzcgNi43NjItMTUuNDM4IDEwLjA4Mi0yNC4yIDEwLjA4MnoiLz48cGF0aCBkPSJNNzY2LjcyNiA5MzUuODE2SDI1Ny4yNzRjLTM1LjAwOCAwLTYzLjQ0NC0yOC4zNTItNjMuNDQ0LTYzLjE4NlY0MTAuODkzYzAtMTguODUgMTUuMjctMzQuMDQ4IDM0LjA0OC0zNC4wNDggMTguODUgMCAzNC4wNDggMTUuMjcgMzQuMDQ4IDM0LjA0OHY0NjEuNzM4aDUzNC43NTJWNDEwLjg5M2MwLTE4Ljg1IDE1LjI3LTM0LjA0OCAzNC4wNDgtMzQuMDQ4IDE4Ljg1IDAgMzQuMDQ4IDE1LjI3IDM0LjA0OCAzNC4wNDh2NDYxLjczOGMwIDM0LjgzMy0yOC41MjYgNjMuMTg2LTYzLjQ0NCA2My4xODZ6Ii8+PC9zdmc+",
        selectedIconBase64: "data:image/svg+xml;base64,PHN2ZyB2aWV3Qm94PSIwIDAgMTAyNCAxMDI0IiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIj48cGF0aCBkPSJNOTYwIDU2OC41NTRjLTguNzYyIDAtMTcuNTIzLTMuMzItMjQuMi0xMC4wODJMNTEyIDEzNC41ODQgODguMiA1NTguNTU2Yy0xMy4zMTggMTMuNDAyLTM0Ljk1NyAxMy40MDItNDguMzYgMC0xMy40MDItMTMuMzE4LTEzLjQwMi0zNC45NTcgMC00OC4zNmw0NDcuOS00NDguNzg0YzEzLjMxOC0xMy40MDIgMzQuOTU3LTEzLjQwMiA0OC4zNiAwTDk4NC4yIDUxMC4xMTJjMTMuNDAyIDEzLjMxOCAxMy40MDIgMzQuOTU3IDAgNDguMzYtNi42NzcgNi43NjItMTUuNDM4IDEwLjA4Mi0yNC4yIDEwLjA4MnoiIGZpbGw9IiMwMDdhZmYiLz48cGF0aCBkPSJNNzY2LjcyNiA5MzUuODE2SDI1Ny4yNzRjLTM1LjAwOCAwLTYzLjQ0NC0yOC4zNTItNjMuNDQ0LTYzLjE4NlY0MTAuODkzYzAtMTguODUgMTUuMjctMzQuMDQ4IDM0LjA0OC0zNC4wNDggMTguODUgMCAzNC4wNDggMTUuMjcgMzQuMDQ4IDM0LjA0OHY0NjEuNzM4aDUzNC43NTJWNDEwLjg5M2MwLTE4Ljg1IDE1LjI3LTM0LjA0OCAzNC4wNDgtMzQuMDQ4IDE4Ljg1IDAgMzQuMDQ4IDE1LjI3IDM0LjA0OCAzNC4wNDh2NDYxLjczOGMwIDM0LjgzMy0yOC41MjYgNjMuMTg2LTYzLjQ0NCA2My4xODZ6IiBmaWxsPSIjMDA3YWZmIi8+PC9zdmc+"
      },
      {
        pagePath: "/pages/scan/index",
        text: "扫码开台",
        iconPath: "scan",
        selectedIconPath: "scan-fill",
        isSpecial: true, // 特殊项标记，可以应用特殊样式
        // 添加对应图标的 Base64 编码，作为加载失败时的备选
        iconBase64: "data:image/svg+xml;base64,PHN2ZyB2aWV3Qm94PSIwIDAgMTAyNCAxMDI0IiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIj48cGF0aCBkPSJNNzg4LjQ0OCAxOTJINjQwdjY0aDE0OC40NDhhMzIgMzIgMCAwIDEgMzIgMzJWNDM2YTQgNCAwIDAgMCA0IDRoNjRhNCA0IDAgMCAwIDQtNFYyODhjMC01My4wMTktNDIuOTgxLTk2LTk2LTk2em0wIDY0MGE2NCA2NCAwIDAgMS02NCA2NEg2NDB2LTY0aDE0OC40NDhhNCA0IDAgMCAwIDQtNFY2ODRhNCA0IDAgMCAxIDQtNGg2NGE0IDQgMCAwIDEgNCA0djE0NGE2NCA2NCAwIDAgMS02NCA2NHpNMzg0IDE5MkgyMzUuNTUyYTk2IDk2IDAgMCAwLTk2IDk2djE0NGE0IDQgMCAwIDAgNCA0aDY0YTQgNCAwIDAgMCA0LTRWMjg4YTMyIDMyIDAgMCAxIDMyLTMySDM4NHYtNjR6bS0xNDguNDQ4IDY0MGE2NCA2NCAwIDAgMS02NC02NFY2ODhhNCA0IDAgMCAxIDQtNGg2NGE0IDQgMCAwIDEgNCA0djE0NGE0IDQgMCAwIDAgNCA0SDM4NHY2NEgyMzUuNTUyek0xOTIgNTc2di0xMjhoNjQwdjEyOEgxOTJ6Ii8+PC9zdmc+",
        selectedIconBase64: "data:image/svg+xml;base64,PHN2ZyB2aWV3Qm94PSIwIDAgMTAyNCAxMDI0IiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIj48cGF0aCBkPSJNNzg4LjQ0OCAxOTJINjQwdjY0aDE0OC40NDhhMzIgMzIgMCAwIDEgMzIgMzJWNDM2YTQgNCAwIDAgMCA0IDRoNjRhNCA0IDAgMCAwIDQtNFYyODhjMC01My4wMTktNDIuOTgxLTk2LTk2LTk2em0wIDY0MGE2NCA2NCAwIDAgMS02NCA2NEg2NDB2LTY0aDE0OC40NDhhNCA0IDAgMCAwIDQtNFY2ODRhNCA0IDAgMCAxIDQtNGg2NGE0IDQgMCAwIDEgNCA0djE0NGE2NCA2NCAwIDAgMS02NCA2NHpNMzg0IDE5MkgyMzUuNTUyYTk2IDk2IDAgMCAwLTk2IDk2djE0NGE0IDQgMCAwIDAgNCA0aDY0YTQgNCAwIDAgMCA0LTRWMjg4YTMyIDMyIDAgMCAxIDMyLTMySDM4NHYtNjR6bS0xNDguNDQ4IDY0MGE2NCA2NCAwIDAgMS02NC02NFY2ODhhNCAwIDAgMSA0LTRoNjRhNCA0IDAgMCAxIDQgNHYxNDRhNCA0IDAgMCAwIDQgNEgzODR2NjRIMjM1LjU1MnpNMTkyIDU3NnYtMTI4aDY0MHYxMjhIMTkyeiIgZmlsbD0id2hpdGUiLz48L3N2Zz4="
      },
      {
        pagePath: "/pages/user/index",
        text: "我的",
        iconPath: "user",
        selectedIconPath: "user-fill",
        // 添加对应图标的 Base64 编码，作为加载失败时的备选
        iconBase64: "data:image/svg+xml;base64,PHN2ZyB2aWV3Qm94PSIwIDAgMTAyNCAxMDI0IiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIj48cGF0aCBkPSJNODk4LjUgOTMzLjVoLTc3YzAtMTgxLjQtMTQ3LjEtMzI4LjUtMzI4LjUtMzI4LjVTMTY0LjUgNzUyLjEgMTY0LjUgOTMzLjVoLTc3YzAtMjIzLjggMTgxLjctNDA1LjUgNDA1LjUtNDA1LjVzNDA1LjUgMTgxLjcgNDA1LjUgNDA1LjV6TTUxMiA1MTJDMzkzLjcgNTEyIDI5OCA0MTYuMyAyOTggMjk4UzM5My43IDg0IDUxMiA4NHMyMTQgOTUuNyAyMTQgMjE0LTk1LjcgMjE0LTIxNCAyMTR6bTAtMzUxYy03NS40IDAtMTM3IDYxLjYtMTM3IDEzN3M2MS42IDEzNyAxMzcgMTM3IDEzNy02MS42IDEzNy0xMzctNjEuNi0xMzctMTM3LTEzN3oiLz48L3N2Zz4=",
        selectedIconBase64: "data:image/svg+xml;base64,PHN2ZyB2aWV3Qm94PSIwIDAgMTAyNCAxMDI0IiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIj48cGF0aCBkPSJNODk4LjUgOTMzLjVoLTc3YzAtMTgxLjQtMTQ3LjEtMzI4LjUtMzI4LjUtMzI4LjVTMTY0LjUgNzUyLjEgMTY0LjUgOTMzLjVoLTc3YzAtMjIzLjggMTgxLjctNDA1LjUgNDA1LjUtNDA1LjVzNDA1LjUgMTgxLjcgNDA1LjUgNDA1LjV6TTUxMiA1MTJDMzkzLjcgNTEyIDI5OCA0MTYuMyAyOTggMjk4UzM5My43IDg0IDUxMiA4NHMyMTQgOTUuNyAyMTQgMjE0LTk1LjcgMjE0LTIxNCAyMTR6IiBmaWxsPSIjMDA3YWZmIi8+PC9zdmc+"
      }
    ] as TabBarItem[]
  },
  
  lifetimes: {
    attached() {
      console.log('自定义 tabBar 组件已附加到页面');
      this.setInitialSelected();
    },
    
    ready() {
      console.log('自定义 tabBar 组件已准备就绪');
      // 在 ready 生命周期调用 setInitialSelected 替代不存在的 show 生命周期
      this.setInitialSelected();
    }
  },
  
  pageLifetimes: {
    // 页面显示时触发
    show() {
      console.log('自定义 tabBar 页面 show 生命周期');
      this.setInitialSelected();
    }
  },
  
  methods: {
    // 设置初始选中状态
    setInitialSelected() {
      const pages = getCurrentPages();
      const currentPage = pages[pages.length - 1];
      if (!currentPage) return;
      
      console.log('当前页面路径:', currentPage.route);
      const route = '/' + currentPage.route;
      const tabIndex = this.data.list.findIndex(item => item.pagePath === route);
      
      console.log('匹配的索引:', tabIndex);
      if (tabIndex !== -1 && tabIndex !== this.data.selected) {
        console.log('设置选中项为:', tabIndex);
        this.setData({ selected: tabIndex });
      }
    },
    
    // tabBar 项点击事件处理函数
    switchTab(e: WechatMiniprogram.TouchEvent) {
      const index = e.currentTarget.dataset.index as number;
      const item = this.data.list[index];
      
      console.log('点击了 tabBar 项:', index, item.text);
      
      // 如果点击当前项，不做跳转
      if (this.data.selected === index) return;
      
      // 跳转到对应页面
      wx.switchTab({
        url: item.pagePath
      });
      
      // 更新选中状态
      this.setData({ selected: index });
    }
  }
}); 