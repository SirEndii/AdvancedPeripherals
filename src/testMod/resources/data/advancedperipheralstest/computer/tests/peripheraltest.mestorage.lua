sleep(4)
bridge = peripheral.wrap("bottom")
test.assert(bridge, "Peripheral not found")

isOnline = bridge.isConnected()
test.assert(isOnline, "Bridge is not connected/system is not online")

energyUsage = bridge.getEnergyUsage()
test.assert(energyUsage > 18, tostring(energyUsage) .. " Consumption does not seem right")

maxEnergyStorage = bridge.getMaxEnergyStorage()
test.assert(maxEnergyStorage == 1608800, tostring(maxEnergyStorage) .. " Max Energy Storage does not seem right")

energyStorage = bridge.getEnergyStorage()
test.assert(energyStorage > 1590000, tostring(energyStorage) .. " Energy Storage does not seem right")

totalItemStorage = bridge.getTotalItemStorage()
test.assert(totalItemStorage == 67264, "Total item storage is not valid")

totalFluidStorage = bridge.getTotalFluidStorage()
test.assert(totalFluidStorage == 81536, "Total fluid storage is not valid")

usedItemStorage = bridge.getUsedItemStorage()
test.assert(usedItemStorage == 1120, "Used item storage is not valid")

usedFluidStorage = bridge.getUsedFluidStorage()
test.assert(usedFluidStorage == 2025, "Used fluid storage is not valid")

availableItemStorage = bridge.getAvailableItemStorage()
test.assert(availableItemStorage == 66144, "Available item storage is not valid")

availableFluidStorage = bridge.getAvailableFluidStorage()
test.assert(availableFluidStorage == 79511, "Available fluid storage is not valid")

cells = bridge.listCells()
test.assert(#cells == 2, "There should be 2 cells")

itemCell = nil
fluidCell = nil
for _, cell in pairs(cells) do
    if cell.cellType == "item" then
        itemCell = cell
    end
end
for _, cell in pairs(cells) do
    if cell.cellType == "fluid" then
        fluidCell = cell
    end
end

test.assert(itemCell, "Item cell not found")
test.assert(fluidCell, "Fluid cell not found")

itemCellBytes = itemCell.totalBytes
test.assert(itemCellBytes == 65536, "Item cell bytes is not valid")

fluidCellBytes = fluidCell.totalBytes
test.assert(fluidCellBytes == 65536, "Fluid cell bytes is not valid")

itemCellItem = itemCell.item
test.assert(itemCellItem == "ae2:item_storage_cell_64k", "Item cell item not found")

fluidCellItem = fluidCell.item
test.assert(fluidCellItem == "ae2:fluid_storage_cell_64k", "Fluid cell item not found")

itemCellBytesPerType = itemCell.bytesPerType
test.assert(itemCellBytesPerType == 512, "Item cell bytes per type is not valid")

fluidCellBytesPerType = fluidCell.bytesPerType
test.assert(fluidCellBytesPerType == 512, "Fluid cell bytes per type is not valid")
