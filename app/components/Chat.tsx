import { Composer } from './Composer';

return (
  <div className="flex flex-row w-full h-full">
    <div className="flex-1">
      {/* 기존 채팅 에디터 코드 */}
      // ... existing code ...
    </div>
    <div className="w-1/3 border-l border-gray-200">
      <Composer />
    </div>
  </div>
); 