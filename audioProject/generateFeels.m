function generateFeels (songName, detectLength, beatFactor, smoothLevel)

    [soundMatrix, sampleRate] = wavread([songName, '.wav']);
    
    soundMatrix = soundMatrix(1:end, 1);
    
    time = (1:length(soundMatrix))';
    
    %subplot(2,2,1);
    
    %plot(time, soundMatrix);
    %title('Signal Amplitude');
    %xlabel('Time');
    %ylabel('Amplitude');    
    
    samplesPerMillisecond = sampleRate / 1000;
    
    %Condense the signal down to 1 millisecond intervals
    
    averages = 1:floor(length(soundMatrix)/ samplesPerMillisecond);
    
    index = 0;
    for i = 1 : floor(length(soundMatrix) / samplesPerMillisecond)
        index = index + 1;
        energy = 0;
        for a = 1 : samplesPerMillisecond
            energy = energy + abs(soundMatrix(i * floor(samplesPerMillisecond) + a));
        end
        averages(floor(index)) = energy / samplesPerMillisecond;
    end
    
    disp('Length in Milliseconds: ');
    disp(length(averages));
    
    disp('Length in min: ');
    disp(length(averages) / 1000 / 60);
    
    smoothedFeels = smooth((averages)', length(averages) / smoothLevel)';
    averageFeel = median(smoothedFeels);
    
    %make the feels heavily skewed from center
    for i = 1 : length(smoothedFeels)
        oldOffset = averageFeel - smoothedFeels(i);
        newOffset = (oldOffset / averageFeel) * .5;
        smoothedFeels(i) =.5 - newOffset;
    end
    
    clf;
    
    subplot(2,1,1)
    plot(1:length(averages), averages);
    title('Avg. Signal Amplitude per ms');
    xlabel('Time (ms)');
    ylabel('Amplitude (feel)');
    
    hold all;
    
    plot(1:length(averages), smoothedFeels);
    
    %find the beats based on history of length detectLength Milliseconds.
    beats = (1 : length(averages)) .* 0;

    for i = 1 : length(averages) - detectLength;
        history = 0;
        for a = i : i + detectLength
            history = history + averages(a);
        end
        history = history / detectLength;
        if averages(i + detectLength) / history > beatFactor
            beats(i)  = 1;
        end
    end
    
    subplot(2,1,2)
    plot(1:length(beats), beats);
    title('Action Moments');
    xlabel('Time (ms)');
    ylabel('Action (0/1)');
    
    quieterSound = soundMatrix .* .05;
    
    for i = 1 : length(beats)
        if beats(i) == 1
            quieterSound(floor(i * samplesPerMillisecond)) = .1;
        end
    end
    clear playsnd
    %sound(quieterSound, sampleRate);
    
    %print to file
    
    fid = fopen([songName, '_beats.kres'],'wt');  % Note the 'wt' for writing in text mode
    fprintf(fid,'%f\n', beats);  % The format string is applied to each element of a
    fclose(fid);
    
    fid = fopen([songName, '_feels.kres'],'wt');  % Note the 'wt' for writing in text mode
    fprintf(fid,'%f\n', smoothedFeels);  % The format string is applied to each element of a
    fclose(fid);
       
end