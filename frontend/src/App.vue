<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">售后赔付系统</div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF">
        <el-menu-item index="/dashboard">
          <i class="el-icon-data-board"></i>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/aftersale">
          <i class="el-icon-document"></i>
          <span>售后单管理</span>
        </el-menu-item>
        <el-menu-item index="/compensation">
          <i class="el-icon-money"></i>
          <span>赔付记录</span>
        </el-menu-item>
        <el-menu-item index="/rules">
          <i class="el-icon-setting"></i>
          <span>赔付规则</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <i class="el-icon-user"></i>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
</script>

<style>
* { margin: 0; padding: 0; }
html, body, #app { height: 100%; }
.layout-container { height: 100%; }
.el-aside { background-color: #304156; color: #fff; }
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  background-color: #2b2f3a;
}
.el-main { background-color: #f0f2f5; padding: 20px; }
</style>
