/**
 * 用户状态管理模块
 * 基于 Pinia 管理 token、用户信息及登录状态，并实现本地持久化
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { getUserInfo, login as loginApi } from '@/api/user.js';

/** 本地存储中 token 的键名 */
const TOKEN_KEY = 'satoken';

/**
 * 用户 Store
 * @returns {import('pinia').StoreDefinition} 用户状态仓库定义
 */
export const useUserStore = defineStore('user', () => {
	/** token 响应式状态 */
	const token = ref(uni.getStorageSync(TOKEN_KEY) || '');
	/** 用户信息响应式状态 */
	const userInfo = ref(null);
	/** 是否已登录的计算属性 */
	const isLogin = computed(() => !!token.value);

	/**
	 * 设置 token 并同步到本地缓存
	 * @param {string} value - 用户 token
	 */
	const setToken = (value) => {
		token.value = value;
		uni.setStorageSync(TOKEN_KEY, value);
	};

	/**
	 * 清除 token 和用户信息，并移除本地缓存
	 */
	const clearToken = () => {
		token.value = '';
		userInfo.value = null;
		uni.removeStorageSync(TOKEN_KEY);
	};

	/**
	 * 用户登录
	 * 登录成功后保存 token 并拉取用户信息
	 * @param {Object} loginData - 登录参数
	 * @returns {Promise<any>} 登录接口返回结果
	 */
	const login = async (loginData) => {
		const res = await loginApi(loginData);
		if (res && res.data) {
			setToken(res.data);
			await fetchUserInfo();
		}
		return res;
	};

	/**
	 * 拉取当前登录用户信息
	 * @returns {Promise<any>} 用户信息接口返回结果
	 */
	const fetchUserInfo = async () => {
		const res = await getUserInfo();
		if (res && res.data) {
			userInfo.value = res.data;
		}
		return res;
	};

	/**
	 * 退出登录
	 */
	const logout = () => {
		clearToken();
	};

	return {
		token,
		userInfo,
		isLogin,
		setToken,
		clearToken,
		login,
		fetchUserInfo,
		logout
	};
});
