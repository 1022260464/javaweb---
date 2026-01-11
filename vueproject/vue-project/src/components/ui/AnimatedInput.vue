<!-- Uses base code from shadcn-vue Input component and extends it's functionality--> 
 <template> 
   <div 
     ref="inputContainerRef" 
     :class="cn('group/input relative rounded-lg transition duration-300', props.containerClass)" 
     @mouseenter="() => (visible = true)" 
     @mouseleave="() => (visible = false)" 
     @mousemove="handleMouseMove" 
   > 
     <!-- 边框光晕层 --> 
     <div 
       v-if="visible" 
       class="absolute inset-0 pointer-events-none rounded-lg" 
       :style="{ 
         background: `conic-gradient(from ${angle}deg at ${mouse.x}px ${mouse.y}px, rgba(59, 130, 246, 0.8), rgba(147, 51, 234, 0.8), rgba(239, 68, 68, 0.8), rgba(59, 130, 246, 0.8))` 
       }" 
     ></div> 
     
     <!-- 输入框遮罩层，用于显示边框光晕 --> 
     <div class="absolute inset-0.5 rounded-lg bg-gray-50 dark:bg-zinc-800 pointer-events-none"></div> 
     
     <!-- 输入框 --> 
     <input 
       v-bind="$attrs" 
       v-model="modelValue" 
       :class=" 
         cn( 
           `flex h-10 w-full border-none bg-transparent text-black dark:text-white px-3 py-2 text-sm  file:border-0 file:bg-transparent 
           file:text-sm file:font-medium placeholder:text-neutral-400 dark:placeholder-text-neutral-600 
           focus-visible:outline-none focus-visible:ring-[2px]  focus-visible:ring-neutral-400 dark:focus-visible:ring-neutral-600 
            disabled:cursor-not-allowed disabled:opacity-50 
            relative z-10`, 
           props.class, 
         ) 
       " 
     /> 
   </div> 
 </template> 
 
 <script setup lang="ts"> 
 import type { HTMLAttributes } from "vue"; 
 import { cn } from "@/lib/utils"; 
 import { useVModel } from "@vueuse/core"; 
 import { ref, onMounted, onUnmounted } from "vue"; 
 
 defineOptions({ 
   inheritAttrs: false, 
 }); 
 
 const props = defineProps<{ 
   defaultValue?: string | number; 
   modelValue?: string | number; 
   class?: HTMLAttributes["class"]; 
   containerClass?: HTMLAttributes["class"]; 
 }>(); 
 
 const emits = defineEmits<{ 
   (e: "update:modelValue", payload: string | number): void; 
 }>(); 
 
 const modelValue = useVModel(props, "modelValue", emits, { 
   passive: true, 
   defaultValue: props.defaultValue, 
 }); 
 
 const inputContainerRef = ref<HTMLDivElement | null>(null); 
 const mouse = ref<{ x: number; y: number }>({ x: 0, y: 0 }); 
 const angle = ref(0); 
 const visible = ref(false); 
 let animationFrame: number;

 function handleMouseMove({ clientX, clientY }: MouseEvent) { 
   if (!inputContainerRef.value) return; 
 
   const { left, top } = inputContainerRef.value.getBoundingClientRect(); 
   mouse.value = { x: clientX - left, y: clientY - top }; 
 } 
 
 function animate() {
   angle.value = (angle.value + 1) % 360;
   animationFrame = requestAnimationFrame(animate);
 }

 onMounted(() => {
   animate();
 });

 onUnmounted(() => {
   cancelAnimationFrame(animationFrame);
 });
 </script> 
 
 <style scoped> 
 input { 
   box-shadow: none !important; 
 } 
 </style>