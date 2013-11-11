
dirPath = 'cur\';
% dirPath = '1380606685734\';

light = csvread([dirPath 'lightData.csv']);
vib   = csvread([dirPath 'vibrationData.csv']);

%norm timebase
light(:,1) = light(:,1) - light(1,1);
vib(:,1)   = vib(:,1)   - vib(1,1);

figure
subplot(2,1,1);
title('light data');
% plot(light(:,1), light(:,2), '-rx');
% axis([0 light(end,1) 0 max(light(:,2)) * 1.2])

subplot(2,1,2);
title('vibration data');
plot(vib(:,1), vib(:,2), '-rx');
axis([0 vib(end,1) 0 max(vib(:,2)) * 1.2])
