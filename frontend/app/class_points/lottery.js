// DOM 元素
const classNameElem = document.getElementById('class-name');
const lotteryContainer = document.getElementById('lottery-container');
const namesContainer = document.getElementById('names-container');
const resultContainer = document.getElementById('result-container');
const resultElem = document.getElementById('result');
const congratulationElem = document.getElementById('congratulation');
const lotteryBtn = document.getElementById('lottery-btn');
const spinSound = document.getElementById('spin-sound');
const winSound = document.getElementById('win-sound');
const fileInput = document.getElementById('file-input');
const fileInputContainer = document.querySelector('.file-input-container');

// 全局变量
let participants = []; // 参与者列表
let groupName = ''; // 组名
let selectedWinner = null; // 被选中的获奖者
let isRolling = false; // 是否正在滚动
let rollInterval = null; // 滚动定时器
let nameElements = []; // 名字元素数组
let currentPosition = 0; // 当前滚动位置
let rollSpeed = 2; // 初始滚动速度(px)
let maxRollSpeed = 15; // 最大滚动速度(px)
let itemHeight = 60; // 每个名字项高度，调整为60px
let visibleItems = 3; // 可见名字数量调整为3
let acceleration = 0.1; // 加速率

// 抽奖文案
const congratulations = [
    "恭喜你！今天的幸运儿就是你啦！",
    "好运常伴！奖品归你所有！",
    "命中注定的大奖得主！",
    "幸运之神眷顾着你！",
    "恭喜你成为今日的幸运星！",
    "好运爆棚！大奖非你莫属！",
    "今天的主角就是你了！",
    "运气MAX！恭喜你获得大奖！",
    "欧皇本皇驾到！",
    "天选之人就是你！",
    "这一刻，你就是最闪亮的星！",
    "好运来袭！恭喜你赢得大奖！"
];

// 初始化
document.addEventListener('DOMContentLoaded', () => {
    // 添加文件输入监听
    fileInput.addEventListener('change', handleFileSelect);
    lotteryBtn.addEventListener('click', handleLotteryBtnClick);
});

// 处理文件选择
function handleFileSelect(event) {
    const file = event.target.files[0];
    if (!file) return;
    
    // 检查文件类型
    if (file.type !== 'text/plain' && !file.name.endsWith('.txt')) {
        alert('请选择txt文本文件');
        return;
    }
    
    const reader = new FileReader();
    
    reader.onload = function(e) {
        try {
            const text = e.target.result;
            processParticipantData(text);
            
            // 确保按钮可用
            lotteryBtn.disabled = false;
            
            // 隐藏文件选择按钮
            hideFileInput();
            
            // 初始化名字列表
            initializeNamesList();
        } catch (error) {
            console.error('处理参与者数据失败:', error);
            alert('处理参与者数据失败，请确保文件格式正确。');
        }
    };
    
    reader.onerror = function() {
        console.error('读取文件失败');
        alert('读取文件失败，请重试。');
    };
    
    reader.readAsText(file);
}

// 隐藏文件输入按钮
function hideFileInput() {
    if (fileInputContainer) {
        fileInputContainer.style.display = 'none';
    }
}

// 处理参与者数据
function processParticipantData(text) {
    const lines = text.trim().split('\n');
    
    if (lines.length > 0) {
        groupName = lines[0].trim();
        classNameElem.textContent = groupName;
        
        participants = lines.slice(1)
            .map(line => line.trim())
            .filter(name => name.length > 0);
            
        console.log(`加载了组 "${groupName}" 的 ${participants.length} 名参与者`);
        
        if (participants.length === 0) {
            throw new Error('没有找到参与者名单');
        }
    } else {
        throw new Error('参与者数据为空');
    }
}

// 初始化名字列表
function initializeNamesList() {
    // 清空容器
    namesContainer.innerHTML = '';
    nameElements = [];
    
    // 确保有足够的名字数量用于滚动
    const containerHeight = namesContainer.clientHeight || 180;
    const requiredNames = Math.ceil(containerHeight / itemHeight) * 3; // 确保至少有3屏的名字
    
    // 如果名单人数太少，复制名单以确保足够数量
    let displayNames = [];
    while (displayNames.length < requiredNames) {
        displayNames = displayNames.concat([...participants]);
    }
    
    // 限制数量，避免过多DOM元素
    displayNames = displayNames.slice(0, Math.max(requiredNames, 30));
    
    // 创建名字元素
    for (let i = 0; i < displayNames.length; i++) {
        const nameItem = document.createElement('div');
        nameItem.className = 'name-item';
        nameItem.textContent = displayNames[i];
        nameItem.style.top = `${i * itemHeight}px`;
        
        namesContainer.appendChild(nameItem);
        nameElements.push(nameItem);
    }
    
    // 初始位置
    currentPosition = 0;
    
    // 更新中心名字
    updateCenterName();
    
    // 打印诊断信息
    console.log(`初始化了 ${nameElements.length} 个名字元素`);
}

// 更新中心高亮名字
function updateCenterName() {
    // 获取中心位置
    const containerHeight = namesContainer.clientHeight;
    const centerPosition = containerHeight / 2;
    
    // 移除所有中心标记
    nameElements.forEach(elem => elem.classList.remove('center'));
    
    // 找到最接近中心的名字并标记为中心
    let closestElem = null;
    let minDistance = Infinity;
    
    nameElements.forEach(elem => {
        const elemTop = parseFloat(elem.style.top);
        const elemCenter = elemTop + itemHeight / 2;
        const distance = Math.abs(elemCenter - centerPosition);
        
        if (distance < minDistance) {
            minDistance = distance;
            closestElem = elem;
        }
    });
    
    // 标记中心名字
    if (closestElem) {
        closestElem.classList.add('center');
    }
}

// 处理抽奖按钮点击
function handleLotteryBtnClick() {
    if (participants.length === 0) {
        alert('没有参与者数据可用，请先选择参与者名单文件');
        return;
    }
    
    // 如果当前正在滚动，则停止滚动
    if (isRolling) {
        stopRolling();
        return;
    }
    
    // 完全重置并重新初始化名字列表
    resetNamesList();
    
    // 开始滚动
    startRolling();
}

// 重置名字列表
function resetNamesList() {
    // 清空容器和数组
    namesContainer.innerHTML = '';
    nameElements = [];
    
    // 重新初始化
    initializeNamesList();
}

// 开始滚动
function startRolling() {
    // 已经在滚动则返回
    if (isRolling) return;
    
    // 更新状态
    isRolling = true;
    
    // 更改按钮样式和文字
    lotteryBtn.querySelector('span').textContent = '停止';
    lotteryBtn.classList.add('stop-btn');
    
    // 播放音效
    playSpinSound();
    
    // 开始动画 - 更低的初始速度
    rollSpeed = 2;
    animateRoll();
}

// 滚动动画
function animateRoll() {
    if (!isRolling) return;
    
    // 逐渐加速，但速度要比之前慢
    if (rollSpeed < maxRollSpeed) {
        rollSpeed += acceleration;
    }
    
    // 移动所有名字
    moveNames();
    
    // 更新中心名字
    updateCenterName();
    
    // 额外确保可见性检查，防止在任何时候出现空白
    if (isRolling) {
        ensureVisibleNames();
    }
    
    // 继续动画
    rollInterval = requestAnimationFrame(animateRoll);
}

// 移动所有名字
function moveNames() {
    // 更新当前位置
    currentPosition += rollSpeed;
    
    // 计算总高度
    const totalHeight = nameElements.length * itemHeight;
    
    // 检查是否需要重置位置计数器以避免数值过大
    if (currentPosition > totalHeight) {
        currentPosition = currentPosition % totalHeight;
    }
    
    // 获取容器高度用于计算可见区域
    const containerHeight = namesContainer.clientHeight;
    
    // 更新所有名字位置
    nameElements.forEach((elem, index) => {
        let newTop = parseFloat(elem.style.top || (index * itemHeight)) - rollSpeed;
        
        // 如果元素滚出了顶部可见区域很远，将其移到底部
        if (newTop < -itemHeight * 2) {
            // 找出最底部元素的位置
            const maxTop = Math.max(...nameElements.map(e => parseFloat(e.style.top || 0)));
            // 将当前元素放到最底部元素之后
            newTop = maxTop + itemHeight;
        }
        
        elem.style.top = `${newTop}px`;
    });
    
    // 确保可见区域内有足够的名字
    ensureVisibleNames();
}

// 停止滚动
function stopRolling() {
    // 标记停止
    isRolling = false;
    
    // 禁用按钮
    lotteryBtn.disabled = true;
    
    // 停止音效
    spinSound.pause();
    spinSound.currentTime = 0;
    
    // 取消动画帧
    cancelAnimationFrame(rollInterval);
    
    // 确保所有名字元素可见
    ensureVisibleNames();
    
    // 开始减速过程
    let slowDownSpeed = rollSpeed;
    const deceleration = 0.1; // 减小减速率，使减速更加平缓
    
    function slowDown() {
        // 减速
        slowDownSpeed = Math.max(0.5, slowDownSpeed - deceleration);
        
        // 更新名字位置
        nameElements.forEach((elem) => {
            let newTop = parseFloat(elem.style.top || 0) - slowDownSpeed;
            
            // 如果元素滚出了顶部可见区域很远，将其移到底部
            if (newTop < -itemHeight * 2) {
                // 找出最底部元素的位置
                const maxTop = Math.max(...nameElements.map(e => parseFloat(e.style.top || 0)));
                // 将当前元素放到最底部元素之后
                newTop = maxTop + itemHeight;
            }
            
            elem.style.top = `${newTop}px`;
        });
        
        // 确保可见区域内有足够的名字
        ensureVisibleNames();
        
        // 更新中心名字
        updateCenterName();
        
        // 继续减速或停止
        if (slowDownSpeed > 0.5) {
            requestAnimationFrame(slowDown);
        } else {
            finishRolling();
        }
    }
    
    // 开始减速
    slowDown();
}

// 确保可见区域内有足够的名字
function ensureVisibleNames() {
    const containerHeight = namesContainer.clientHeight;
    
    // 检查可见区域内的名字数量
    const visibleElements = nameElements.filter(elem => {
        const top = parseFloat(elem.style.top || 0);
        return top >= -itemHeight && top < containerHeight;
    });
    
    // 确保有至少一屏多一点的名字可见
    const minRequired = visibleItems + 2; // 3个可见项 + 2个缓冲
    
    // 如果可见区域内名字太少，添加更多名字到合适位置
    if (visibleElements.length < minRequired) {
        // 找出当前最底部元素的位置
        let maxTop = -Infinity;
        nameElements.forEach(elem => {
            const top = parseFloat(elem.style.top || 0);
            if (top > maxTop) maxTop = top;
        });
        
        // 如果没有找到有效的最大值，使用一个默认值
        if (maxTop === -Infinity) {
            maxTop = containerHeight;
        }
        
        // 添加更多名字到底部
        const namesToAdd = Math.min(5, minRequired - visibleElements.length + 3); // 多加几个确保足够
        
        for (let i = 0; i < namesToAdd; i++) {
            const newIndex = nameElements.length % participants.length;
            const nameItem = document.createElement('div');
            nameItem.className = 'name-item';
            nameItem.textContent = participants[newIndex];
            nameItem.style.top = `${maxTop + (i+1) * itemHeight}px`;
            
            namesContainer.appendChild(nameItem);
            nameElements.push(nameItem);
        }
        
        console.log(`添加了 ${namesToAdd} 个名字元素，当前总数: ${nameElements.length}`);
    }
}

// 完成抽奖
function finishRolling() {
    // 播放中奖音效
    playWinSound();
    
    // 对齐中心名字
    alignToCenterName();
    
    // 标记结果
    const centerElem = document.querySelector('.name-item.center');
    if (centerElem) {
        centerElem.classList.remove('center');
        centerElem.classList.add('result');
        selectedWinner = centerElem.textContent;
        
        // 去除其他名字的动画效果
        nameElements.forEach(elem => {
            if (elem !== centerElem) {
                elem.style.opacity = '0.5';
            }
        });
    }
    
    // 创建彩色纸屑效果
    createConfetti();
    
    // 恢复按钮状态
    setTimeout(() => {
        lotteryBtn.querySelector('span').textContent = '开始点名';
        lotteryBtn.classList.remove('stop-btn');
        lotteryBtn.disabled = false;
    }, 800);
}

// 对齐中心名字
function alignToCenterName() {
    const centerElem = document.querySelector('.name-item.center');
    if (!centerElem) return;
    
    // 计算容器中心
    const containerHeight = namesContainer.clientHeight;
    const centerPosition = containerHeight / 2;
    
    // 计算中心元素的当前位置
    const elemTop = parseFloat(centerElem.style.top);
    const elemCenter = elemTop + itemHeight / 2;
    
    // 计算需要调整的距离
    const adjustment = centerPosition - elemCenter;
    
    // 调整所有元素位置
    nameElements.forEach(elem => {
        const currentTop = parseFloat(elem.style.top);
        elem.style.top = `${currentTop + adjustment}px`;
    });
    
    // 更新当前位置
    currentPosition -= adjustment;
}

// 创建彩色纸屑效果
function createConfetti() {
    const particleCount = 80;
    const colors = ['#4a6baf', '#5ac8fa', '#5e5ce6', '#e74c3c', '#ffcc00', '#4cd964'];
    
    for (let i = 0; i < particleCount; i++) {
        setTimeout(() => {
            const particle = document.createElement('div');
            particle.className = 'firework';
            
            // 随机大小
            const size = 6 + Math.random() * 6;
            particle.style.width = `${size}px`;
            particle.style.height = `${size}px`;
            
            // 随机颜色
            const randomColor = colors[Math.floor(Math.random() * colors.length)];
            particle.style.backgroundColor = randomColor;
            
            // 随机位置 - 从顶部掉落
            const startX = Math.random() * 100;
            particle.style.left = `${startX}%`;
            particle.style.top = '-10px';
            
            lotteryContainer.appendChild(particle);
            
            // 纸屑动画 - 掉落并摇摆
            const duration = 1000 + Math.random() * 2000;
            const fallDistance = 100 + Math.random() * 150;
            const swingDistance = 50 - Math.random() * 100;
            
            particle.animate(
                [
                    { 
                        transform: 'translateY(0) translateX(0) rotate(0deg)', 
                        opacity: 1
                    },
                    { 
                        transform: `translateY(${fallDistance * 0.5}px) translateX(${swingDistance * 0.5}px) rotate(${180 * Math.random()}deg)`, 
                        opacity: 0.8, 
                        offset: 0.5 
                    },
                    { 
                        transform: `translateY(${fallDistance}px) translateX(${swingDistance}px) rotate(${360 * Math.random()}deg)`, 
                        opacity: 0 
                    }
                ],
                {
                    duration: duration,
                    easing: 'cubic-bezier(0.4, 0, 0.2, 1)',
                    fill: 'forwards'
                }
            ).onfinish = () => {
                if (particle.parentNode) {
                    particle.parentNode.removeChild(particle);
                }
            };
        }, Math.random() * 500);
    }
}

// 播放旋转音效
function playSpinSound() {
    spinSound.currentTime = 0;
    spinSound.loop = true;
    spinSound.play().catch(error => {
        console.error('播放音效失败:', error);
    });
}

// 播放中奖音效
function playWinSound() {
    spinSound.loop = false;
    spinSound.pause();
    spinSound.currentTime = 0;
    
    winSound.currentTime = 0;
    winSound.play().catch(error => {
        console.error('播放音效失败:', error);
    });
}