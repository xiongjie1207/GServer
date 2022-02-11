/**
 * Copyright (c) 2015-2016, James Xiong 熊杰 (xiongjie.cn@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by xiongjie on 2016/12/22.
 */

package com.wegame.aop;

import com.wegame.aop.annotation.Around;


/**
 * InterceptorBuilder
 */
public class InterceptorBuilder {

	private static final Interceptor[] NULL_INTERS = new Interceptor[0];


	/**
	 * Build Interceptors.
	 * <pre>
	 * Interceptors of action:  finalInters = globalInters + classInters + methodInters
	 * Interceptors of service: finalInters = globalInters + injectInters + classInters + methodInters
	 * </pre>
	 */
	public static Interceptor[] build(Around annotation) {
		Interceptor[] methodInters = createInterceptors(annotation);
		return methodInters;
	}

	private static Interceptor[] createInterceptors(Around annotation) {
		if (annotation == null)
			return NULL_INTERS;

		Class<? extends Interceptor>[] interceptorClasses = annotation.value();
		if (interceptorClasses.length == 0)
			return NULL_INTERS;

		Interceptor[] result = new Interceptor[interceptorClasses.length];
		try {
			for (int i=0; i<result.length; i++) {
				result[i] = interceptorClasses[i].newInstance();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}
}
