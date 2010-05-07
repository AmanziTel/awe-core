module KPI
  def sum_new(data)
    if data.respond_to? 'sum_internal'
          data.count_internal
        elsif data.respond_to? 'each'
      s=0.0
      data.each do |element|
        s+=element
      end
      s
    end
  end

  def count(data)
    if data.respond_to? 'count_internal'
      data.count_internal
    elsif data.respond_to? 'each'
      s=0
      data.each do |element|
        s+=1
      end
      s
    end
  end

  def average(data)
    #begin time
    start=Time.now.usec
    a=data.sum/data.count
    #end time
    puts "[average]Time: #{Time.now.usec-start}"
    a
  end

  def average_opt(data)
    #begin time
    start=Time.now.usec
    if data.respond_to? 'each'
      n=0
      s=0.0
      data.each do |element|
        s+=element
        n+=1
      end
      #check division by zero
      a=s/n
      #end time
      puts "[average_opt]Time: #{Time.now.usec-start}"
      a
    end
  end

  def harmean(data)
    if data.respond_to? 'each'
      n=0
      s=0.0
      data.each do |element|
        s+=1.0/element
        n+=1
      end
      #check division by zero
      1.0/(s/n)
    end
  end

  def geomean(data)
    if data.respond_to? 'each'
      n=0
      s=1
      data.each do |element|
        s*=element
        n+=1
      end
      #check division by zero
      root(s,n)
    end
  end

  def squareroot(number)
    Math.sqrt(number)
  end

  def product(a,b)
    a*b
  end

  def power(a,b)
    a**b
  end

  def root(number,n)
    number**(1.0/n)
  end

  def round(number)
    if number.respond_to? 'round'
      number.round
    end
  end

  def trunc(number)
    if number.respond_to? 'truncate'
      number.truncate
    end
  end

  def log10(number)
    Math.log10(number)
  end

  def log(number)
    Math.log(number)
  end

  def maximum(data)
    if data.respond_to? 'max'
      data.max
    elsif data.respond_to? 'each'
      max=Float::MIN
      data.each do |element|
        max=element if element>=max
      end
      max
    end
  end

  def minimum(data)
    if data.respond_to? 'min'
      data.min
    elsif data.respond_to? 'each'
      min=Float::MAX
      data.each do |element|
        min=element if element<=min
      end
      min
    end
  end

  def avedev(data)
    if data.respond_to? 'each'
      avg=average_opt(data)
      sum=0.0
      n=0
      data.each do |element|
        sum+=(element-avg).abs
        n+=1
      end
      sum/n
    end
  end

  def stdev(data)
    if data.respond_to? 'each'
      avg=average_opt(data)
      sum=0.0
      n=0
      data.each do |element|
        sum+=(element-avg)**2
        n+=1
      end
      squareroot(sum/(n-1))
    end
  end

  def seriessum(x,n,m,coefficients)
    i=1
    sum=0.0
    coefficients.each do |coeff|
      sum+=coeff*x**(n+(i-1)*m)
      i+=1
    end
    sum
  end

  def subtotal(data, func)
    if data.respond_to? func
      KPI.send(func, data)
    end
  end

  def mediana(data)
    arr=Array.new
    data.each do |element|
      arr<<element #if element.is_a? Fixnum
    end
    n=arr.length
    arr=arr.sort
    n%2!=0 ? arr[(n+1)/2-1] : (arr[n/2-1]+arr[n/2]).to_f/2
  end

  def percentile(data,p)
    arr=[]
    data.each do |element|
      arr<<element
    end
    arr=arr.sort
    len=arr.length
    p=p.to_f/100 if p>1.0
    n=p*(len-1)+1
    k=n.truncate
    d=n-k
    if n==1
      arr[0]
    elsif n==len
      arr[len-1]
    else
      arr[k-1]+d*(arr[k]-arr[k-1])
    end
  end

  def probability(data,probabilities,low,high=nil)
    n_arr=[]
    data.each do |element|
      n_arr<<element
    end
    p_arr=[]
    probabilities.each do |prob|
      p_arr<<prob
    end
    len=n_arr.length
    if len==p_arr.length
      sum=0.0
      for i in 0..len-1
        if high!=nil
          sum+=p_arr[i] if n_arr[i]>=low and n_arr[i]<=high
        else
          sum+=p_arr[i] if n_arr[i]==low
        end
      end
      sum
    else
      raise "Incorrect data: different length for numbers and probabilities!"
    end
  end
end
