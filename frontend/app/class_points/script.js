// DOM 元素
const classNameElem = document.getElementById('class-name');
const animationArea = document.getElementById('animation-area');
const resultContainer = document.getElementById('result-container');
const resultElem = document.getElementById('result');
const memeTextElem = document.getElementById('meme-text');
const rollBtn = document.getElementById('roll-btn');
const rollSound = document.getElementById('roll-sound');
const finishSound = document.getElementById('finish-sound');
const fileInput = document.getElementById('file-input');
const fileInputContainer = document.querySelector('.file-input-container');

// 全局变量
let students = [];
let className = '';
let selectedStudent = null;
let isRolling = false;
let animationFrames = [];
let nameElements = [];
let rollAnimationInterval = null; // 用于存储点名动画定时器
let activeAnimationsCount = 0; // 跟踪活动动画数量
const MAX_ACTIVE_ANIMATIONS = 25; // 同时存在的最大动画数量
let currentInterval = 300; // 当前名字生成的时间间隔(ms)
let isSlowingDown = false; // 是否正在减速
const SPEED_UP_RATE = 15; // 加速的速率(ms)
const SLOW_DOWN_RATE = 60; // 减速的速率(ms)，更大的值表示减速更快
const MAX_SLOW_INTERVAL = 400; // 减速到这个时间间隔就停止(ms)

// 网络热梗文本
const memes = [
    "别慌，是时候展示真正的技术了！",
    "这波啊，这波是肉眼可见的心慌",
    "刚才就你话最多，就你了",
    "我看你有话要说，那你来吧",
    "看起来今天是你的幸运日呢～",
    "天选之子驾到，快让让！",
    "这个问题就由你为大家解答吧！",
    "不愧是你，就知道会选到你！",
    "你已经被老师的雷达锁定了！",
    "此时此刻你的内心：慌得一批",
    "恭喜你获得今日回答问题特权！",
    "总有人要站出来，今天是你了！",
    "相信自己，你可以的！",
    "show time",
    "你已经成功引起了我的注意！",
    "你已经成功引起了我的注意！",
    "你已经成功引起了我的注意！",
    "你已经成功引起了我的注意！",
    "你已经成功引起了我的注意！"
];

// 初始化
document.addEventListener('DOMContentLoaded', () => {
    // 添加文件输入监听
    fileInput.addEventListener('change', handleFileSelect);
    rollBtn.addEventListener('click', handleRollBtnClick);
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
            processStudentData(text);
            
            // 确保按钮可用
            rollBtn.disabled = false;
            
            // 隐藏文件选择按钮
            hideFileInput();
        } catch (error) {
            console.error('处理学生数据失败:', error);
            alert('处理学生数据失败，请确保文件格式正确。');
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

// 处理学生数据
function processStudentData(text) {
    const lines = text.trim().split('\n');
    
    if (lines.length > 0) {
        className = lines[0].trim();
        classNameElem.textContent = className;
        
        students = lines.slice(1)
            .map(line => line.trim())
            .filter(name => name.length > 0);
            
        console.log(`加载了班级 "${className}" 的 ${students.length} 名学生`);
        
        if (students.length === 0) {
            throw new Error('没有找到学生名单');
        }
    } else {
        throw new Error('学生数据为空');
    }
}

// 处理点名按钮点击
function handleRollBtnClick() {
    if (students.length === 0) {
        alert('没有学生数据可用，请先选择学生名单文件');
        return;
    }
    
    // 如果当前正在点名，则停止点名
    if (isRolling) {
        // 开始减速过程
        isSlowingDown = true;
        // 立即禁用按钮，直到整个流程完成
        rollBtn.disabled = true;
        return;
    }
    
    // 开始点名
    isRolling = true;
    isSlowingDown = false;
    
    // 更改按钮样式和文字
    rollBtn.querySelector('span').textContent = '停止';
    rollBtn.classList.add('stop-btn');
    
    // 隐藏上一次的结果
    resultContainer.style.display = 'none';
    
    // 隐藏文件选择按钮
    hideFileInput();
    
    // 清除之前的名字元素
    clearNameElements();
    
    // 播放音效
    playRollSound();
    
    // 重置时间间隔为初始较慢的速度
    currentInterval = 300;
    
    // 开始点名动画
    startRollAnimation();
}

// 开始点名动画
function startRollAnimation() {
    // 首次创建名字
    createRandomNameElement();
    
    // 创建动画并持续显示随机名字直到手动停止，频率逐渐增加
    const speedUpInterval = setInterval(() => {
        if (!isRolling) {
            clearInterval(speedUpInterval);
            return;
        }
        
        // 逐渐减小时间间隔，增加频率，最小到70ms
        currentInterval = Math.max(70, currentInterval - 15);
        
    }, 200); // 每200ms调整一次速度
    
    // 使用递归函数和setTimeout实现动态间隔
    function scheduleNextName() {
        if (!isRolling) return;
        
        // 如果正在减速
        if (isSlowingDown) {
            // 逐渐增加时间间隔，降低频率（增加间隔60ms，加快减速过程）
            currentInterval = Math.min(400, currentInterval + 60);
            
            // 如果间隔已经很大，表示已经足够慢，可以停止了
            if (currentInterval >= 400) {
                stopRollAnimationComplete();
                return;
            }
        }
        
        // 如果活动动画数量超过阈值，稍微延迟
        if (activeAnimationsCount > MAX_ACTIVE_ANIMATIONS) {
            setTimeout(scheduleNextName, currentInterval);
            return;
        }
        
        // 创建一个随机名字元素
        createRandomNameElement();
        
        // 安排下一个名字
        setTimeout(scheduleNextName, currentInterval);
    }
    
    // 开始生成名字
    scheduleNextName();
}

// 创建随机名字元素
function createRandomNameElement() {
    // 随机选择一个学生名字
    const randomIndex = Math.floor(Math.random() * students.length);
    const name = students[randomIndex];
    
    // 创建并添加名字元素
    createNameElement(name);
}

// 创建名字元素
function createNameElement(name) {
    activeAnimationsCount++; // 增加活动动画计数
    
    const nameElem = document.createElement('div');
    nameElem.className = 'name-animation';
    nameElem.textContent = name;
    
    // 随机位置
    const left = 10 + Math.random() * 80; // 10% - 90% 的位置
    const top = Math.random() * 20; // 起始位置在顶部
    
    nameElem.style.left = `${left}%`;
    nameElem.style.top = `${top}%`;
    
    // 随机旋转
    const rotation = -10 + Math.random() * 20; // -10度到10度的旋转
    const scale = 0.8 + Math.random() * 0.4; // 0.8到1.2的缩放
    
    animationArea.appendChild(nameElem);
    nameElements.push(nameElem);
    
    // 添加动画
    requestAnimationFrame(() => {
        nameElem.style.opacity = '1';
        nameElem.style.transform = `scale(${scale}) rotateZ(${rotation}deg) rotateX(0deg)`;
        
        // 设置下落动画
        const animationDuration = 1.5 + Math.random(); // 1.5-2.5s
        const fallAnimation = nameElem.animate(
            [
                { 
                    top: `${top}%`, 
                    opacity: 1, 
                    transform: `scale(${scale}) rotateZ(${rotation}deg) rotateX(0deg)`,
                    offset: 0 
                },
                { 
                    top: '55%', 
                    opacity: 0.9, 
                    transform: `scale(${scale * 1.1}) rotateZ(${rotation * 0.8}deg) rotateX(5deg)`,
                    offset: 0.5 
                },
                { 
                    top: '110%', 
                    opacity: 0, 
                    transform: `scale(${scale * 0.9}) rotateZ(${rotation * 1.2}deg) rotateX(10deg)`,
                    offset: 1 
                }
            ],
            {
                duration: animationDuration * 1000,
                easing: 'cubic-bezier(0.4, 0.0, 1, 1)',
                fill: 'forwards'
            }
        );
        
        // 动画结束后自动移除元素
        fallAnimation.onfinish = () => {
            if (nameElem.parentNode) {
                nameElem.parentNode.removeChild(nameElem);
                // 从数组中移除这个元素
                const index = nameElements.indexOf(nameElem);
                if (index > -1) {
                    nameElements.splice(index, 1);
                }
                // 减少活动动画计数
                activeAnimationsCount--;
            }
        };
        
        animationFrames.push(fallAnimation);
    });
}

// 停止点名动画完成
function stopRollAnimationComplete() {
    // 停止名字生成的定时器
    isRolling = false;
    isSlowingDown = false;
    
    // 禁用按钮，直到整个流程完成
    rollBtn.disabled = true;
    
    // 停止音效
    rollSound.pause();
    rollSound.currentTime = 0;
    
    // 选择一个随机学生
    const randomIndex = getSecureRandomInt(0, students.length - 1);
    selectedStudent = students[randomIndex];
    
    // 允许现有动画平滑完成，然后再展示最终结果
    setTimeout(() => {
        // 平滑地淡出现有动画
        smoothlyFadeOutAnimations();
    }, 100);
}

// 平滑淡出现有动画
function smoothlyFadeOutAnimations() {
    // 如果没有剩余动画，直接显示结果
    if (nameElements.length === 0) {
        showFinalNamesTransition();
        return;
    }
    
    // 为所有现有动画创建淡出效果
    const fadeOutPromises = nameElements.map(elem => {
        return new Promise(resolve => {
            const fadeOut = elem.animate(
                [
                    { opacity: parseFloat(window.getComputedStyle(elem).opacity) || 1 },
                    { opacity: 0 }
                ],
                {
                    duration: 300,
                    easing: 'ease-out',
                    fill: 'forwards'
                }
            );
            
            fadeOut.onfinish = () => {
                resolve();
            };
        });
    });
    
    // 等待所有淡出动画完成
    Promise.all(fadeOutPromises)
        .then(() => {
            // 清除所有元素
            clearNameElements();
            // 显示过渡动画
            showFinalNamesTransition();
        })
        .catch(() => {
            // 出错时也要确保显示结果
            clearNameElements();
            showFinalNamesTransition();
        });
}

// 展示最后几个名字的过渡动画
function showFinalNamesTransition() {
    // 清除当前所有名字
    clearNameElements();
    
    // 创建3个渐进减速的名字动画，最后一个是被选中的学生
    const transitionNames = [];
    
    // 添加两个随机名字
    for (let i = 0; i < 2; i++) {
        const randomIndex = Math.floor(Math.random() * students.length);
        if (students[randomIndex] !== selectedStudent) {
            transitionNames.push(students[randomIndex]);
        } else {
            // 如果随机到了选中的学生，选择另一个
            const nextIndex = (randomIndex + 1) % students.length;
            transitionNames.push(students[nextIndex]);
        }
    }
    
    // 最后一个是选中的学生
    transitionNames.push(selectedStudent);
    
    // 依次显示这些名字，速度越来越慢
    showTransitionName(transitionNames, 0);
}

// 递归显示过渡名字
function showTransitionName(names, index) {
    if (index >= names.length) {
        // 所有过渡名字都显示完毕，展示最终结果
        setTimeout(() => {
            showResult();
        }, 200);
        return;
    }
    
    // 创建并显示当前名字
    const nameElem = document.createElement('div');
    nameElem.className = 'name-animation transition-name';
    nameElem.textContent = names[index];
    
    // 居中显示
    nameElem.style.left = '50%';
    nameElem.style.top = '50%';
    nameElem.style.transform = 'translate(-50%, -50%) scale(1.5)';
    
    // 最后一个名字(选中的学生)要特殊处理
    if (index === names.length - 1) {
        nameElem.classList.add('final-name');
    }
    
    animationArea.appendChild(nameElem);
    nameElements.push(nameElem);
    
    // 名字出现的动画
    nameElem.animate(
        [
            { opacity: 0, transform: 'translate(-50%, -50%) scale(0.8)' },
            { opacity: 1, transform: 'translate(-50%, -50%) scale(1.5)' }
        ],
        {
            duration: 200,
            easing: 'ease-out',
            fill: 'forwards'
        }
    );
    
    // 根据索引增加延迟，实现减速效果
    const delay = 300 + index * 200;
    
    // 延迟后显示下一个名字
    setTimeout(() => {
        // 最后一个名字特殊处理 - 不要淡出
        if (index === names.length - 1) {
            // 最后一个名字显示1秒后，再显示最终结果
            setTimeout(() => {
                // 渐隐最后一个名字
                nameElem.animate(
                    [
                        { opacity: 1, transform: 'translate(-50%, -50%) scale(1.5)' },
                        { opacity: 0, transform: 'translate(-50%, -50%) scale(2)' }
                    ],
                    {
                        duration: 500,
                        easing: 'ease-in',
                        fill: 'forwards'
                    }
                ).onfinish = () => showResult();
            }, 1000);
            return;
        }
        
        // 淡出当前名字
        const fadeOut = nameElem.animate(
            [
                { opacity: 1, transform: 'translate(-50%, -50%) scale(1.5)' },
                { opacity: 0, transform: 'translate(-50%, -50%) scale(2)' }
            ],
            {
                duration: 300,
                easing: 'ease-in',
                fill: 'forwards'
            }
        );
        
        fadeOut.onfinish = () => {
            // 如果不是最后一个名字，显示下一个
            if (index < names.length - 1) {
                showTransitionName(names, index + 1);
            }
        };
    }, delay);
}

// 显示结果
function showResult() {
    // 清除所有动画元素
    clearNameElements();
    
    // 播放完成音效
    playFinishSound();
    
    // 设置结果
    resultElem.textContent = selectedStudent;
    
    // 随机选择一个热梗
    const randomMemeIndex = Math.floor(Math.random() * memes.length);
    memeTextElem.textContent = memes[randomMemeIndex];
    
    // 显示结果容器
    resultContainer.style.display = 'flex';
    
    // 确保文字可见
    resultElem.style.opacity = '1';
    resultElem.style.transform = 'scale(1)';
    
    // 添加简单的淡入动画
    resultElem.animate(
        [
            { opacity: 0, transform: 'scale(0.9)' },
            { opacity: 1, transform: 'scale(1)' }
        ],
        {
            duration: 600,
            easing: 'ease-out',
            fill: 'forwards'
        }
    );
    
    // 确保即使动画失败，结果也会显示
    setTimeout(() => {
        resultElem.style.opacity = '1';
        resultElem.style.transform = 'scale(1)';
        
        // 确保结果容器显示
        if (resultContainer.style.display !== 'flex') {
            resultContainer.style.display = 'flex';
        }
    }, 100);
    
    // 轻微的浮动动画效果
    setTimeout(() => {
        resultElem.animate(
            [
                { transform: 'translateY(0)' },
                { transform: 'translateY(-5px)' },
                { transform: 'translateY(0)' }
            ],
            {
                duration: 3000,
                iterations: Infinity,
                easing: 'ease-in-out'
            }
        );
    }, 800);
    
    // 热梗文本动画
    setTimeout(() => {
        memeTextElem.style.opacity = '1';
        memeTextElem.style.transform = 'translateY(0)';
    }, 600);
    
    // 添加粒子效果
    setTimeout(() => {
        createCelebrateEffect();
    }, 300);
    
    // 重置按钮状态，允许再次点名
    setTimeout(() => {
        // 恢复按钮样式和文字
        rollBtn.querySelector('span').textContent = '开始点名';
        rollBtn.classList.remove('stop-btn');
        rollBtn.disabled = false;
    }, 1200);
}

// 创建庆祝效果
function createCelebrateEffect() {
    const particleCount = 50;
    
    for (let i = 0; i < particleCount; i++) {
        setTimeout(() => {
            const particle = document.createElement('div');
            particle.className = 'particle';
            particle.style.position = 'absolute';
            particle.style.width = '8px';
            particle.style.height = '8px';
            particle.style.borderRadius = '50%';
            
            // 随机颜色
            const colors = ['#ff2d55', '#5e5ce6', '#ff9500', '#ffcc00', '#34c759', '#007aff'];
            const randomColor = colors[Math.floor(Math.random() * colors.length)];
            particle.style.backgroundColor = randomColor;
            
            // 随机位置
            const startX = 50;
            const startY = 50;
            particle.style.left = `calc(${startX}% - 4px)`;
            particle.style.top = `calc(${startY}% - 4px)`;
            particle.style.opacity = '1';
            
            animationArea.appendChild(particle);
            
            // 随机方向和距离
            const angle = Math.random() * Math.PI * 2;
            const distance = 10 + Math.random() * 50;
            const destX = startX + Math.cos(angle) * distance;
            const destY = startY + Math.sin(angle) * distance;
            
            // 粒子动画
            particle.animate(
                [
                    { 
                        transform: 'scale(0)', 
                        opacity: 0 
                    },
                    { 
                        transform: 'scale(1)', 
                        opacity: 1, 
                        offset: 0.1 
                    },
                    { 
                        transform: 'scale(0.8)', 
                        opacity: 0.8, 
                        left: `calc(${destX}% - 4px)`, 
                        top: `calc(${destY}% - 4px)`, 
                        offset: 0.8 
                    },
                    { 
                        transform: 'scale(0)', 
                        opacity: 0, 
                        left: `calc(${destX}% - 4px)`, 
                        top: `calc(${destY}% - 4px)` 
                    }
                ],
                {
                    duration: 1000 + Math.random() * 1000,
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

// 播放点名音效
function playRollSound() {
    rollSound.currentTime = 0;
    rollSound.play().catch(error => {
        console.error('播放音效失败:', error);
    });
}

// 播放完成音效
function playFinishSound() {
    finishSound.currentTime = 0;
    finishSound.play().catch(error => {
        console.error('播放音效失败:', error);
    });
}

// 安全的随机数生成函数
function getSecureRandomInt(min, max) {
    // 如果可用，使用加密安全的随机数生成器
    if (window.crypto && window.crypto.getRandomValues) {
        const range = max - min + 1;
        const bytesNeeded = Math.ceil(Math.log2(range) / 8);
        const maxNum = Math.pow(256, bytesNeeded);
        const array = new Uint8Array(bytesNeeded);
        
        let randomNum;
        do {
            window.crypto.getRandomValues(array);
            randomNum = 0;
            for (let i = 0; i < bytesNeeded; i++) {
                randomNum = (randomNum * 256) + array[i];
            }
        } while (randomNum >= maxNum - (maxNum % range));
        
        return min + (randomNum % range);
    } else {
        // 回退到标准的随机数生成
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }
}

// 清除名字元素
function clearNameElements() {
    // 停止所有动画
    animationFrames.forEach(animation => {
        if (animation && animation.cancel) {
            animation.cancel();
        }
    });
    animationFrames = [];
    
    // 移除所有名字元素
    nameElements.forEach(elem => {
        if (elem && elem.parentNode) {
            elem.parentNode.removeChild(elem);
        }
    });
    nameElements = [];
    
    // 重置活动动画计数
    activeAnimationsCount = 0;
    
    // 清空动画区域
    animationArea.innerHTML = '';
} 