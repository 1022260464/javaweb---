<template>
  <canvas ref="canvas" class="w-full h-full"></canvas>
</template>

<script setup>
import { onMounted, onUnmounted, ref, watch, nextTick } from 'vue';

const emit = defineEmits(['webgl-error']);

const props = defineProps({
  shaderCode: {
    type: String,
    required: true
  },
  hue: { type: Number, default: 300 },
  saturation: { type: Number, default: 0.5 },
  brightness: { type: Number, default: 1 },
  speed: { type: Number, default: 1 }
});

const canvas = ref(null);
let animationFrameId = null;
let gl = null;
let program = null;
let startTime = 0;

// 顶点着点器源码
const vertexShaderSource = `
attribute vec2 a_position;
void main() {
  gl_Position = vec4(a_position, 0.0, 1.0);
}
`;

// 编译着色器
const compileShader = (gl, type, source) => {
  const shader = gl.createShader(type);
  gl.shaderSource(shader, source);
  gl.compileShader(shader);
  
  if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
    console.error('An error occurred compiling the shaders:', gl.getShaderInfoLog(shader));
    gl.deleteShader(shader);
    return null;
  }
  
  return shader;
};

// 创建WebGL程序
const createProgram = (gl, vertexSource, fragmentSource) => {
  const vertexShader = compileShader(gl, gl.VERTEX_SHADER, vertexSource);
  if (!vertexShader) {
    console.error('Failed to compile vertex shader');
    return null;
  }
  
  const fragmentShader = compileShader(gl, gl.FRAGMENT_SHADER, fragmentSource);
  if (!fragmentShader) {
    console.error('Failed to compile fragment shader');
    gl.deleteShader(vertexShader);
    return null;
  }
  
  const program = gl.createProgram();
  gl.attachShader(program, vertexShader);
  gl.attachShader(program, fragmentShader);
  gl.linkProgram(program);
  
  if (!gl.getProgramParameter(program, gl.LINK_STATUS)) {
    console.error('Unable to initialize the shader program:', gl.getProgramInfoLog(program));
    gl.deleteProgram(program);
    gl.deleteShader(vertexShader);
    gl.deleteShader(fragmentShader);
    return null;
  }
  
  return program;
};

// 设置着色器uniform变量
const setUniforms = (gl, program, width, height, time) => {
  // 设置时间uniform
  const iTimeLocation = gl.getUniformLocation(program, 'iTime');
  if (iTimeLocation !== -1 && iTimeLocation !== null) {
    gl.uniform1f(iTimeLocation, time);
  }
  
  // 设置分辨率uniform
  const iResolutionLocation = gl.getUniformLocation(program, 'iResolution');
  if (iResolutionLocation !== -1 && iResolutionLocation !== null) {
    gl.uniform2f(iResolutionLocation, width, height);
  }
  
  // 设置鼠标位置uniform
  const iMouseLocation = gl.getUniformLocation(program, 'iMouse');
  if (iMouseLocation !== -1 && iMouseLocation !== null) {
    gl.uniform3f(iMouseLocation, 0, 0, 0);
  }
};

// 初始化WebGL
const initWebGL = () => {
  if (!canvas.value) {
    console.error('Canvas element not found');
    return false;
  }
  
  // 获取WebGL上下文，尝试多种方式
  const contextAttributes = {
    antialias: false,
    alpha: false,
    preserveDrawingBuffer: false
  };
  
  gl = canvas.value.getContext('webgl', contextAttributes) || 
       canvas.value.getContext('experimental-webgl', contextAttributes) ||
       canvas.value.getContext('webgl2', contextAttributes);
  
  if (!gl) {
    console.error('Unable to get WebGL context. Your browser may not support WebGL.');
    // 添加降级处理：显示简单的CSS背景
    canvas.value.style.background = 'linear-gradient(45deg, #667eea 0%, #764ba2 100%)';
    emit('webgl-error');
    return false;
  }
  
  console.log('WebGL context initialized successfully');
  
  // 设置canvas尺寸
  const resizeCanvas = () => {
    if (!canvas.value || !gl) return;
    const width = canvas.value.clientWidth;
    const height = canvas.value.clientHeight;
    canvas.value.width = width;
    canvas.value.height = height;
    gl.viewport(0, 0, width, height);
  };
  
  resizeCanvas();
  window.addEventListener('resize', resizeCanvas);
  
  // 创建并使用程序
  program = createProgram(gl, vertexShaderSource, props.shaderCode);
  if (!program) {
    console.error('Failed to create WebGL program');
    return false;
  }
  
  gl.useProgram(program);
  
  // 设置顶点缓冲区
  const positionBuffer = gl.createBuffer();
  gl.bindBuffer(gl.ARRAY_BUFFER, positionBuffer);
  gl.bufferData(gl.ARRAY_BUFFER, new Float32Array([
    -1.0, -1.0,
     1.0, -1.0,
    -1.0,  1.0,
    -1.0,  1.0,
     1.0, -1.0,
     1.0,  1.0
  ]), gl.STATIC_DRAW);
  
  // 启用顶点属性
  const positionLocation = gl.getAttribLocation(program, 'a_position');
  gl.enableVertexAttribArray(positionLocation);
  gl.vertexAttribPointer(positionLocation, 2, gl.FLOAT, false, 0, 0);
  
  // 记录开始时间
  startTime = performance.now();
  
  // 清理事件监听器
  const cleanup = () => {
    window.removeEventListener('resize', resizeCanvas);
  };
  
  // 返回清理函数
  return cleanup;
};

// 渲染函数
const render = () => {
  if (!gl || !program || !canvas.value) {
    animationFrameId = requestAnimationFrame(render);
    return;
  }
  
  const currentTime = (performance.now() - startTime) / 1000 * props.speed;
  const width = canvas.value.width;
  const height = canvas.value.height;
  
  // 设置uniform值
  setUniforms(gl, program, width, height, currentTime);
  
  // 渲染
  gl.clear(gl.COLOR_BUFFER_BIT);
  gl.drawArrays(gl.TRIANGLES, 0, 6);
  
  animationFrameId = requestAnimationFrame(render);
};

// 监听着色器代码变化
watch(() => props.shaderCode, () => {
  if (gl && program) {
    // 重新创建程序
    try {
      const newProgram = createProgram(gl, vertexShaderSource, props.shaderCode);
      if (newProgram) {
        gl.useProgram(newProgram);
        gl.deleteProgram(program);
        program = newProgram;
        console.log('Shader updated successfully');
      } else {
        console.error('Failed to update shader');
      }
    } catch (error) {
      console.error('Failed to update shader:', error);
    }
  }
});

let cleanupFn = null;

onMounted(async () => {
  console.log('ShaderToy component mounted');
  // 确保DOM已经更新
  await nextTick();
  
  // 延迟初始化，确保canvas元素完全渲染
  setTimeout(() => {
    cleanupFn = initWebGL();
    if (cleanupFn) {
      render();
    } else {
      // WebGL初始化失败，显示降级背景
      console.warn('WebGL initialization failed, using fallback background');
      if (canvas.value) {
        canvas.value.style.background = 'linear-gradient(45deg, #667eea 0%, #764ba2 100%)';
      }
    }
  }, 100);
});

onUnmounted(() => {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId);
  }
  
  if (cleanupFn) {
    cleanupFn();
  }
  
  if (gl && program) {
    gl.deleteProgram(program);
  }
});
</script>