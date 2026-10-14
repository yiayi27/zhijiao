module.exports = {
  types: [
    { value: '新增', name: '✨ 新增:    一项新功能' },
    { value: '修复', name: '🐛 修复:    一个Bug' },
    { value: '优化', name: '⚡️ 优化:    提升性能或体验' },
    { value: '文档', name: '📝 文档:    仅文档变更' },
    { value: '格式', name: '💄 格式:    代码格式（不影响代码运行的变动）' },
    { value: '重构', name: '♻️ 重构:    代码重构（非新增功能也非修复Bug）' },
    { value: '测试', name: '✅ 测试:    添加或修改测试' },
    { value: '回滚', name: '⏪ 回滚:    回滚到上一个版本' },
    { value: '配置', name: '⚙️ 配置:    修改项目配置、构建流程等' },
    { value: '其他', name: '🧹 其他:    对构建过程或辅助工具和库的更改（不影响源文件、测试用例）' },
    { value: '构建', name: '🏗️ 构建:    影响构建系统或外部依赖的更改（例如：gulp、npm）' },
    { value: 'CI', name: '🤖 CI:       CI配置文件和脚本的更改（例如：Travis, Circle）' },
  ],
  scopes: [
    { name: '(无范围)', value: '' },
    { name: '全局范围', value: '全局范围' },
    { name: '公共组件', value: '公共组件' },
    { name: '用户模块', value: '用户模块' },
    { name: '订单模块', value: '订单模块' },
    { name: '商品模块', value: '商品模块' },
    { name: '其他', value: '其他' },
    // 您可以在此添加更多常用范围
  ],
  // override the messages, defaults are as follows
  messages: {
    type: '请选择提交类型:',
    scope: '请选择或输入一个范围 (可选):',
    customScope: '请输入自定义的修改范围:',
    subject: '请简要描述提交(必填):',
    body: '请输入详细描述 (可选，使用 "|" 换行):',
    breaking: '列出任何重大变更 (可选):',
    footer: '列出此次改动关闭的所有ISSUES (可选，例如: #31, #34):',
    confirmCommit: '确认提交?',
  },

  allowCustomScopes: true, // 允许输入不在列表中的自定义范围
  allowEmptyScopes: true, // 允许范围为空 (通过选择 '无范围')
  allowBreakingChanges: ['新增', '修复', '优化', '重构'], // 对应中文的 value
  // skip any questions you want
  skipQuestions: ['body', 'footer'], // 跳过详细描述和页脚问题

  // limit subject length
  subjectLimit: 100,
  breaklineChar: '|', // It is supported for description. Just a hint
}
