<template>
  <div class="forget">
    <el-form ref="forgetRef" :model="forgetForm" :rules="forgetRules" class="forget-form">
      <h3 class="title">重置密码</h3>
      <el-form-item prop="username">
        <el-input v-model="forgetForm.username" type="text" size="large" auto-complete="off" placeholder="请输入账号">
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="email">
        <el-input v-model="forgetForm.email" type="email" size="large" auto-complete="off" placeholder="请输入邮箱">
          <template #prefix><svg-icon icon-class="email" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="emailCode">
        <el-input v-model="forgetForm.emailCode" size="large" auto-complete="off" placeholder="邮箱验证码" style="width: 63%">
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <el-button class="forget-code-btn" type="primary" :disabled="isEmailCodeSending || emailCodeTimer > 0" @click="sendEmailCode">
          {{ emailCodeTimer > 0 ? `${emailCodeTimer}秒后重新获取` : '获取验证码' }}
        </el-button>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input v-model="forgetForm.code" size="large" auto-complete="off" placeholder="验证码" style="width: 63%">
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <div class="forget-code">
          <img :src="codeUrl" class="forget-code-img" @click="getCode" />
        </div>
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="forgetForm.password" type="password" size="large" auto-complete="off" placeholder="请输入新密码">
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input v-model="forgetForm.confirmPassword" type="password" size="large" auto-complete="off" placeholder="请确认新密码">
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item style="width: 100%">
        <el-button :loading="loading" size="large" type="primary" style="width: 100%" @click.prevent="handleReset">
          <span v-if="!loading">重置密码</span>
          <span v-else>重置中...</span>
        </el-button>
        <div style="float: right; margin-top: 10px">
          <router-link class="link-type" :to="{ path: '/login', query: redirect ? { redirect } : undefined }">返回登录</router-link>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { getCodeImg, resetPassword, sendEmailVerifyCode } from '@/api/login';
import { ElMessage } from 'element-plus';
import { to } from 'await-to-js';
import { useRouter, useRoute } from 'vue-router';
import { computed } from 'vue';

interface ForgetFormData {
  username: string;
  email: string;
  emailCode: string;
  password: string;
  confirmPassword: string;
  code: string;
  uuid: string;
}

const router = useRouter();
const route = useRoute();

// 获取原始的 redirect 参数
const redirect = computed(() => route.query.redirect as string);

const forgetForm = ref<ForgetFormData>({
  username: '',
  email: '',
  emailCode: '',
  password: '',
  confirmPassword: '',
  code: '',
  uuid: ''
});

// 邮箱验证码相关
const isEmailCodeSending = ref(false);
const emailCodeTimer = ref(0);
const emailTimerInterval = ref<number>();

const captchaEnabled = ref(import.meta.env.VITE_APP_CAPTCHA_ENABLE === 'true');

const validatePassword = (rule: any, value: string, callback: Function) => {
  if (value.length < 6) {
    callback(new Error('密码不能小于6位'));
  } else {
    callback();
  }
};

const validateConfirmPassword = (rule: any, value: string, callback: Function) => {
  if (value !== forgetForm.value.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

// 邮箱格式验证
const validateEmail = (rule: any, value: string, callback: Function) => {
  const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
  if (!value) {
    callback(new Error('请输入邮箱'));
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入正确的邮箱格式'));
  } else {
    callback();
  }
};

const forgetRules = {
  username: [{ required: true, trigger: 'blur', message: '请输入您的账号' }],
  email: [
    { required: true, trigger: 'blur', message: '请输入邮箱' },
    { validator: validateEmail, trigger: 'blur' }
  ],
  emailCode: [{ required: false, trigger: 'blur', message: '请输入邮箱验证码' }],
  password: [
    { required: true, trigger: 'blur', message: '请输入新密码' },
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, trigger: 'blur', message: '请再次输入新密码' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  code: [{ required: false, trigger: 'change', message: '请输入验证码' }]
};

const codeUrl = ref('');
const loading = ref(false);
const forgetRef = ref();

const getCode = async () => {
  if (!captchaEnabled.value) {
    return;
  }
  const res = await getCodeImg();
  const { data } = res;
  codeUrl.value = 'data:image/gif;base64,' + data.img;
  forgetForm.value.uuid = data.uuid;
};

// 发送邮箱验证码
const sendEmailCode = async () => {
  try {
    // 验证邮箱格式
    await forgetRef.value?.validateField('email');
    isEmailCodeSending.value = true;

    const [err] = await to(
      sendEmailVerifyCode({
        username: forgetForm.value.username,
        email: forgetForm.value.email
      })
    );

    if (!err) {
      ElMessage.success('验证码已发送至您的邮箱');
      // 开始倒计时
      emailCodeTimer.value = 60;
      emailTimerInterval.value = window.setInterval(() => {
        if (emailCodeTimer.value > 0) {
          emailCodeTimer.value--;
        } else {
          if (emailTimerInterval.value) {
            window.clearInterval(emailTimerInterval.value);
          }
        }
      }, 1000);
    }
  } catch (error) {
    console.error('发送邮箱验证码失败:', error);
    ElMessage.error('发送验证码失败，请重试');
  } finally {
    isEmailCodeSending.value = false;
  }
};

const handleReset = () => {
  forgetRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true;
      try {
        const [err] = await to(
          resetPassword({
            username: forgetForm.value.username,
            email: forgetForm.value.email,
            emailCode: forgetForm.value.emailCode,
            password: forgetForm.value.password,
            code: forgetForm.value.code,
            uuid: forgetForm.value.uuid
          })
        );

        if (!err) {
          ElMessage.success('密码重置成功');
          // 直接跳转到登录页面，不带任何参数
          await router.replace('/login');
        } else {
          // 重新获取验证码
          await getCode();
        }
      } catch (error) {
        console.error('重置密码失败:', error);
        ElMessage.error('重置密码失败，请重试');
        // 重新获取验证码
        await getCode();
      } finally {
        loading.value = false;
      }
    }
  });
};

onMounted(() => {
  getCode();
});

// 组件销毁时清除定时器
onBeforeUnmount(() => {
  if (emailTimerInterval.value) {
    window.clearInterval(emailTimerInterval.value);
  }
});
</script>

<style lang="scss" scoped>
.forget {
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

.forget-form {
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

.forget-code {
  width: 33%;
  height: 40px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.forget-code-img {
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

.forget-code-btn {
  width: 33%;
  height: 40px;
  margin-left: 4%;
  font-size: 14px;
}
</style>
