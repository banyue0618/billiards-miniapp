<template>
  <div class="login">
    <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">{{ title }}</h3>
      <el-form-item v-if="tenantEnabled" prop="tenantId">
        <el-select
          v-model="loginForm.tenantId"
          filterable
          remote
          :remote-method="searchTenant"
          :loading="tenantLoading"
          reserve-keyword
          placeholder="请输入公司名称搜索"
          style="width: 100%"
        >
          <el-option v-for="item in tenantList" :key="item.tenantId" :label="item.companyName" :value="item.tenantId"></el-option>
          <template #prefix><svg-icon icon-class="company" class="el-input__icon input-icon" /></template>
        </el-select>
      </el-form-item>
      <el-form-item prop="username">
        <el-input v-model="loginForm.username" type="text" size="large" auto-complete="off" placeholder="账号">
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="loginForm.password" type="password" size="large" auto-complete="off" placeholder="密码" @keyup.enter="handleLogin">
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item v-if="captchaEnabled" prop="code">
        <el-input v-model="loginForm.code" size="large" auto-complete="off" placeholder="验证码" style="width: 63%" @keyup.enter="handleLogin">
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" class="login-code-img" @click="getCode" />
        </div>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" style="margin: 0 0 25px 0">记住密码</el-checkbox>
      <el-form-item style="width: 100%">
        <el-button :loading="loading" size="large" type="primary" style="width: 100%" @click.prevent="handleLogin">
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
        <div style="display: flex; justify-content: flex-end; margin-top: 10px; gap: 20px">
          <router-link class="link-type" :to="'/forget'" style="margin-right: 20px">忘记密码</router-link>
          <router-link v-if="register" class="link-type" :to="'/register'">立即注册</router-link>
          <router-link class="link-type" :to="'/tenantApply'">申请成为租户</router-link>
          <!--          <router-link class="link-type" :to="'/merchantApply'">申请成为商户</router-link>-->
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { getCodeImg, getTenantList, searchTenant as apiSearchTenant } from '@/api/login';
import { useUserStore } from '@/store/modules/user';
import { LoginData, TenantVO } from '@/api/types';
import { to } from 'await-to-js';

const userStore = useUserStore();
const router = useRouter();

const loginForm = ref<LoginData>({
  tenantId: '',
  username: '',
  password: '',
  rememberMe: false,
  code: '',
  uuid: ''
} as LoginData);

const loginRules: ElFormRules = {
  tenantId: [{ required: true, trigger: 'blur', message: '请输入您的租户编号' }],
  username: [{ required: true, trigger: 'blur', message: '请输入您的账号' }],
  password: [{ required: true, trigger: 'blur', message: '请输入您的密码' }],
  code: [{ required: true, trigger: 'change', message: '请输入验证码' }]
};

const codeUrl = ref('');
const loading = ref(false);

// 注册开关
const register = ref(import.meta.env.VITE_APP_REGISTER_ENABLE === 'true');
const redirect = ref('/');
const loginRef = ref<ElFormInstance>();
// 租户列表
const tenantList = ref<TenantVO[]>([]);

const captchaEnabled = ref(import.meta.env.VITE_APP_CAPTCHA_ENABLE === 'true');
const tenantEnabled = ref(import.meta.env.VITE_APP_TENANT_ENABLE === 'true');

// 添加这一行，从环境变量获取标题
const title = ref(import.meta.env.VITE_APP_TITLE);

// 远程搜索相关
const tenantLoading = ref(false);
const allTenantsCache = ref<TenantVO[] | null>(null);

watch(
  () => router.currentRoute.value,
  (newRoute: any) => {
    redirect.value = newRoute.query && newRoute.query.redirect ? decodeURIComponent(newRoute.query.redirect) : '/';
  },
  { immediate: true }
);

const handleLogin = () => {
  loginRef.value?.validate(async (valid: boolean, fields: any) => {
    if (valid) {
      loading.value = true;
      // 勾选了需要记住密码设置在 localStorage 中设置记住用户名和密码
      if (loginForm.value.rememberMe) {
        localStorage.setItem('tenantId', String(loginForm.value.tenantId));
        localStorage.setItem('username', String(loginForm.value.username));
        localStorage.setItem('password', String(loginForm.value.password));
        localStorage.setItem('rememberMe', String(loginForm.value.rememberMe));
      } else {
        // 否则移除
        localStorage.removeItem('tenantId');
        localStorage.removeItem('username');
        localStorage.removeItem('password');
        localStorage.removeItem('rememberMe');
      }
      // 调用action的登录方法
      const [err] = await to(userStore.login(loginForm.value));
      if (!err) {
        console.log('----->' + redirect.value);
        const redirectUrl = redirect.value || '/';
        await router.push(redirectUrl);
        loading.value = false;
      } else {
        loading.value = false;
        // 重新获取验证码
        if (captchaEnabled.value) {
          await getCode();
        }
      }
    } else {
      console.log('error submit!', fields);
    }
  });
};

/**
 * 获取验证码
 */
const getCode = async () => {
  if (!captchaEnabled.value) {
    return;
  }
  const res = await getCodeImg();
  const { data } = res;
  captchaEnabled.value = data.captchaEnabled === undefined ? true : data.captchaEnabled;
  if (captchaEnabled.value) {
    codeUrl.value = 'data:image/gif;base64,' + data.img;
    loginForm.value.uuid = data.uuid;
  }
};

const getLoginData = () => {
  const rememberMe = localStorage.getItem('rememberMe');
  if (!rememberMe) {
    console.log('未获取到记住用户名密码');
    return;
  }
  console.log('获取到记住用户名密码');
  const tenantId = localStorage.getItem('tenantId');
  const username = localStorage.getItem('username');
  const password = localStorage.getItem('password');

  loginForm.value = {
    tenantId: tenantId === null ? String(loginForm.value.tenantId) : tenantId,
    username: username === null ? String(loginForm.value.username) : username,
    password: password === null ? String(loginForm.value.password) : String(password),
    rememberMe: rememberMe === null ? false : Boolean(rememberMe)
  } as LoginData;
};

/**
 * 初始化租户元信息（是否启用、多域名唯一预填）
 */
const initTenantList = async () => {
  // if (!tenantEnabled.value) {
  //   return;
  // }
  // const { data } = await getTenantList();
  // tenantEnabled.value = data.tenantEnabled === undefined ? true : data.tenantEnabled;
  // if (!tenantEnabled.value) {
  //   return;
  // }
  // // 仅当后端基于域名已筛到唯一租户时，直接预填并展示该项
  // if (Array.isArray(data.voList) && data.voList.length === 1) {
  //   tenantList.value = data.voList;
  //   loginForm.value.tenantId = data.voList[0].tenantId;
  //   // 也缓存到本地，后续搜索可复用
  //   allTenantsCache.value = data.voList;
  // }
};

/**
 * 远程搜索租户（后端关键词接口）
 */
const searchTenant = async (query: string) => {
  if (!tenantEnabled.value) {
    return;
  }
  const keyword = (query || '').trim();
  if (keyword.length < 3) {
    tenantList.value = [];
    return;
  }
  tenantLoading.value = true;
  try {
    const { data } = await apiSearchTenant({ keyword, limit: 20 });
    tenantList.value = Array.isArray(data) ? data : [];
  } finally {
    tenantLoading.value = false;
  }
};

onMounted(() => {
  getCode();
  initTenantList();
  getLoginData();
});
</script>

<style lang="scss" scoped>
.login {
  display: flex;
  justify-content: right;
  align-items: center;
  height: 100%;
  background-image: url('../assets/images/login-background.jpg');
  background-size: 100% 100%;
}

.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: var(--el-color-primary);
  font-weight: 600;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;

  margin-right: 12%;
  .el-input {
    height: 40px;

    input {
      height: 40px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0px;
  }
}

.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}

.login-code {
  width: 33%;
  height: 40px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial, serif;
  font-size: 12px;
  letter-spacing: 1px;
}

.login-code-img {
  height: 40px;
  padding-left: 12px;
}

.link-type {
  color: var(--el-color-primary);
  text-decoration: none;
  &:hover {
    color: var(--el-color-primary-light-3);
  }
}
</style>
