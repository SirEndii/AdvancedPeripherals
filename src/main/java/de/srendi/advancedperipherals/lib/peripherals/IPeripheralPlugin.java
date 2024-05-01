/*
 *     Copyright 2024 Intelligence Modding @ https://intelligence-modding.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.srendi.advancedperipherals.lib.peripherals;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.asm.PeripheralMethod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public interface IPeripheralPlugin {
    default List<BoundMethod> getMethods() {
        return PeripheralMethod.GENERATOR.getMethods(this.getClass()).stream()
                .map(named -> new BoundMethod(this, named)).collect(Collectors.toList());
    }

    default @Nullable IPeripheralOperation<?>[] getOperations() {
        return null;
    }

    default boolean isSuitable(IPeripheral peripheral) {
        return true;
    }
}
