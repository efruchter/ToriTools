function generateFeels (songName, detectLength, beatFactor, smoothLevel, beatSpacing)
warning off all;
[soundMatrix, sampleRate] = wavread([songName, '.wav']);
    
    soundMatrix = soundMatrix(1:end, 1);
    
    %time = (1:length(soundMatrix))';
    
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
    
    %find the beats based on history of length detectLength Milliseconds.
    beats = (1 : length(averages)) .* 0;
    beatWait = 0;
    for i = detectLength + 1 : length(averages);
        beatWait = beatWait - 1;
        if beatWait < 0
            if averages(i) / mean((averages(i - detectLength : i))) > beatFactor
                beats(i) = 1;
                beatWait = beatSpacing + 1;
            end
        end
    end
    %{
        make a skewed vector of feel based on beat values
        skewedFeels = 1 : length(beats);
        for i = 1 : length(beats);
            skewedFeels(i) = (beats(i) * 2 + averages(i)) / 2;
        end
        skewedFeels = smooth((skewedFeels)', length(skewedFeels) / smoothLevel)';
    %}
    skewedFeels = smooth((averages)', length(averages) / smoothLevel)';
    skewedFeels = smooth((skewedFeels)', 1000)';
    
    to1Scalar = 1 / max(skewedFeels);
    skewedFeels = skewedFeels .* to1Scalar;
    
    clf;
    
    subplot(3,1,1)
    plot(1:length(averages), averages);
    title('Avg. Signal Amplitude per ms');
    xlabel('Time (ms)');
    ylabel('Amplitude (feel)');
    
    hold all;
    
    % plot what will be the feel levels
    plot(1:length(skewedFeels), skewedFeels);
    
    % plot beats
    subplot(3,1,2)
    plot(1:length(beats), beats);
    title('Action Moments');
    xlabel('Time (ms)');
    ylabel('Action (0/1)');
    
    %Plot fundamental frequency stuff.
    freqs = (1:(length(soundMatrix)/(sampleRate)));
    ms2=sampleRate/1000;                % maximum speech Fx at 1000Hz
    ms20=sampleRate/50;                 % minimum speech Fx at 50Hz
    for i = 1 : length(freqs)
        samplesToAnalyze = soundMatrix((i-1)*(sampleRate) + 1 : i*(sampleRate));
        fundFreq = findFundFreq(samplesToAnalyze, sampleRate, ms2, ms20);
        freqs(i) = fundFreq;
    end
    [maxValue, index] = max(freqs);
    freqs = freqs ./ maxValue;
    subplot(3,1,3)
    plot(1:length(freqs), freqs);
    title('Fundamental Frequencies');
    xlabel('Time (s)');
    ylabel('Fundamental Frequency (Hz)');
    
    
    %print to file
    
    fid = fopen([songName, '_beats.kres'],'wt');
    fprintf(fid,'%f\n', beats);
    fclose(fid);
    
    fid = fopen([songName, '_feels.kres'],'wt');
    fprintf(fid,'%f\n', skewedFeels);
    fclose(fid);
    
    fid = fopen([songName, '_fund.kres'], 'wt');
    fprintf(fid, '%f/n', freqs);
    fclose(fid);
       
end

function fundFreq = findFundFreq(x, fs, ms2, ms20)
    r=xcorr(x,ms20,'coeff');
    r=r(ms20+1:2*ms20+1);
    [rmax,tx]=max(r(ms2:ms20));
    %fprintf('rmax=%g Fx=%gHz\n',rmax,fs/(ms2+tx-1));
    fundFreq = fs/(ms2 + tx - 1);
end