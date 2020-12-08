//--------------------------------------------------------------------------
// This file provides HLSL keyword equivalence for PSSL up to version 1.700 
// This file is provided as-is, without support and does not guarantee full 
// compatibility with HLSL
//--------------------------------------------------------------------------
#ifdef __PSSL__

// system semantics
#define	SV_TARGET					S_TARGET_OUTPUT
#define	SV_Target					S_TARGET_OUTPUT
#define	SV_Target0					S_TARGET_OUTPUT0
#define	SV_Target1					S_TARGET_OUTPUT1
#define	SV_Target2					S_TARGET_OUTPUT2
#define	SV_Target3					S_TARGET_OUTPUT3
#define	SV_Target4					S_TARGET_OUTPUT4
#define	SV_Target5					S_TARGET_OUTPUT5
#define	SV_Target6					S_TARGET_OUTPUT6
#define	SV_Target7					S_TARGET_OUTPUT7

#define	SV_POSITION					S_POSITION
#define	SV_Position					S_POSITION
#define	SV_Depth					S_DEPTH_OUTPUT
#define	SV_DepthGreaterEqual		S_DEPTH_GE_OUTPUT
#define	SV_DepthLessEqual			S_DEPTH_LE_OUTPUT

#define	SV_VertexID					S_VERTEX_ID
#define	SV_InstanceID				S_INSTANCE_ID	
#define	SV_PrimitiveID				S_PRIMITIVE_ID
#define	SV_SampleIndex				S_SAMPLE_INDEX
#define SV_GSInstanceID				S_GSINSTANCE_ID

// parameter modifiers
#define shared						/*NOT_SUPPORTED_BY_PSSL*/
#define groupshared					thread_group_memory
#define nointerpolation				nointerp
#define noperspective				nopersp

// control flow
#define call						call_sub
#define forcecase					force_switch

// tessellation semantics
#define	SV_TessFactor				S_EDGE_TESS_FACTOR
#define	SV_InsideTessFactor			S_INSIDE_TESS_FACTOR
#define	SV_OutputControlPointID		S_OUTPUT_CONTROL_POINT_ID
#define	SV_DomainLocation			S_DOMAIN_LOCATION

// raster semantics
#define	SV_IsFrontFace				S_FRONT_FACE
#define SV_Coverage					S_COVERAGE
#define	SV_ClipDistance				S_CLIP_DISTANCE
#define	SV_CullDistance				S_CULL_DISTANCE
#define	SV_RenderTargetArrayIndex	S_RENDER_TARGET_INDEX
#define	SV_ViewportArrayIndex		S_VIEWPORT_INDEX

// compute semantics
#define	SV_DispatchThreadID			S_DISPATCH_THREAD_ID
#define	SV_GroupID					S_GROUP_ID
#define	SV_GroupIndex				S_GROUP_INDEX
#define	SV_GroupThreadID			S_GROUP_THREAD_ID

// buffer types
#define	Buffer						DataBuffer
#define	RWBuffer					RW_DataBuffer
#define	ByteAddressBuffer			ByteBuffer
#define	RWByteAddressBuffer			RW_ByteBuffer
#define	StructuredBuffer			RegularBuffer
#define	RWStructuredBuffer			RW_RegularBuffer
#define	AppendStructuredBuffer		AppendRegularBuffer
#define	ConsumeStructuredBuffer		ConsumeRegularBuffer
#define	RWTexture1D					RW_Texture1D
#define	RWTexture1DArray			RW_Texture1D_Array
#define	RWTexture2D					RW_Texture2D
#define	RWTexture2DArray			RW_Texture2D_Array
#define	RWTexture3D					RW_Texture3D
#define	Texture1DArray				Texture1D_Array
#define	Texture2DArray				Texture2D_Array
#define	TextureCubeArray			TextureCube_Array
#define Texture2DMS					MS_Texture2D
#define Texture2DMSArray			MS_Texture2D_Array

// buffer methods
#define mips							MipMaps
#define IncrementCounter				IncrementCount
#define	DecrementCounter				DecrementCount
#define SampleCmpLevelZero				SampleCmpLOD0
#define SampleLevel						SampleLOD
#define SampleGrad						SampleGradient
#define CalculateLevelOfDetail			GetLOD
#define CalculateLevelOfDetailUnclamped	GetLODUnclamped
#define GetSamplePosition				GetSamplePoint

// shader resource types
#define	cbuffer						ConstantBuffer
#define	tbuffer						TextureBuffer

// system attributes
#define	domain						DOMAIN_PATCH_TYPE
#define	maxtessfactor				MAX_TESS_FACTOR
#define	outputcontrolpoints			OUTPUT_CONTROL_POINTS
#define	outputtopology				OUTPUT_TOPOLOGY_TYPE
#define	partitioning				PARTITIONING_TYPE
#define	patchconstantfunc			PATCH_CONSTANT_FUNC
#define	instance					INSTANCE
#define	numthreads					NUM_THREADS
#define patchsize					PATCH_SIZE
#define maxvertexcount				MAX_VERTEX_COUNT			
#define earlydepthstencil			FORCE_EARLY_DEPTH_STENCIL

// barrier functions
#define	GroupMemoryBarrier					ThreadGroupMemoryBarrier
#define	GroupMemoryBarrierWithGroupSync		ThreadGroupMemoryBarrierSync
#define DeviceMemoryBarrier					SharedMemoryBarrier
#define DeviceMemoryBarrierWithGroupSync	SharedMemoryBarrierSync
#define AllMemoryBarrier					MemoryBarrier
#define AllMemoryBarrierWithGroupSync		MemoryBarrierSync

// atomic operations
#define InterlockedAdd				AtomicAdd
#define InterlockedAnd				AtomicAnd
#define InterlockedCompareExchange	AtomicCmpExchange
#define InterlockedCompareStore		AtomicCmpStore
#define InterlockedExchange			AtomicExchange
#define InterlockedMax				AtomicMax
#define InterlockedMin				AtomicMin
#define InterlockedOr				AtomicOr
#define InterlockedXor				AtomicXor

// bit operations
#define firstbithigh				FirstSetBit_Hi
#define firstbitlow					FirstSetBit_Lo
#define reversebits					ReverseBits
#define countbits					CountSetBits
#define D3DCOLORtoUBYTE4			FloatColorsToRGBA8

#define EvaluateAttributeAtCentroid		EvaluateAttributeCentroid
#define GetRenderTargetSampleCount		sampleCount
#define GetRenderTargetSamplePosition	samplePosition

// geometry shader
#define TriangleStream				TriangleBuffer				
#define PointStream					PointBuffer					
#define LineStream					LineBuffer					
#define triangle					Triangle					
#define point						Point						
#define line						Line						
#define triangleadj					AdjacentTriangle			
#define lineadj						AdjacentLine

// multimedia operations
#define msad4	// HLSL msad4 signature isn't supported by PSSL, instead use the following
				// uint  msad(uint src0, uint src1, uint acc)
				// uint2 msad(uint2 src0, uint2 src1, uint2 acc)
				// uint3 msad(uint3 src0, uint3 src1, uint3 acc)
				// uint4 msad(uint4 src0, uint4 src1, uint4 acc)

// not supported by PSSL
#define abort
#define errorf
#define printf

#endif  // __PSSL__